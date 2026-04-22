"""Evaluate before/after strategy-model predictions.

This script compares prediction files against the generated gold JSONL test set
and writes a Markdown report suitable for project documentation.
"""

from __future__ import annotations

import argparse
import json
from dataclasses import dataclass, field
from pathlib import Path
from typing import Any


CORE_FIELDS = [
    "metricType",
    "operatorType",
    "actionType",
    "actionValue",
    "timeLimitEnabled",
]


@dataclass
class Metrics:
    total: int = 0
    json_valid: int = 0
    schema_valid: int = 0
    should_suggest_correct: int = 0
    exact_match: int = 0
    metric_correct: int = 0
    action_correct: int = 0
    threshold_count: int = 0
    threshold_abs_error_total: float = 0.0
    threshold_within_10pct: int = 0
    time_window_count: int = 0
    time_window_correct: int = 0
    conflict_count: int = 0
    conflict_avoided: int = 0
    errors: list[str] = field(default_factory=list)

    def pct(self, value: int, denominator: int | None = None) -> float:
        denom = self.total if denominator is None else denominator
        return 0.0 if denom == 0 else value / denom * 100

    @property
    def threshold_mae(self) -> float:
        if self.threshold_count == 0:
            return 0.0
        return self.threshold_abs_error_total / self.threshold_count


def parse_json_maybe(value: Any) -> dict[str, Any] | None:
    if isinstance(value, dict):
        return value
    if not isinstance(value, str):
        return None
    try:
        parsed = json.loads(value)
    except json.JSONDecodeError:
        return None
    return parsed if isinstance(parsed, dict) else None


def is_schema_valid(value: dict[str, Any]) -> bool:
    if set(value.keys()) == {"shouldSuggest"} and value.get("shouldSuggest") is False:
        return True
    required = {
        "shouldSuggest",
        "detected",
        "strategyName",
        "metricType",
        "operatorType",
        "thresholdMin",
        "actionType",
        "actionValue",
        "timeLimitEnabled",
        "startTime",
        "endTime",
        "reason",
    }
    if not required.issubset(value.keys()):
        return False
    if value.get("shouldSuggest") is not True:
        return False
    if value.get("metricType") not in {"LIGHT", "TEMPERATURE", "HUMIDITY"}:
        return False
    if value.get("operatorType") not in {"LT", "GT", "EQ"}:
        return False
    if value.get("actionType") not in {"AUTO_LIGHT", "AUTO_FAN", "NOTIFY_USER"}:
        return False
    if value.get("actionValue") not in {"ON", "OFF", "LOW", "HIGH", "INFO", "WARNING", "DANGER"}:
        return False
    if not isinstance(value.get("thresholdMin"), (int, float)):
        return False
    if not isinstance(value.get("timeLimitEnabled"), bool):
        return False
    return True


def load_gold(path: Path) -> dict[str, dict[str, Any]]:
    records: dict[str, dict[str, Any]] = {}
    with path.open("r", encoding="utf-8") as file:
        for line in file:
            if not line.strip():
                continue
            record = json.loads(line)
            record["gold"] = json.loads(record["assistant"])
            records[record["id"]] = record
    return records


def load_predictions(path: Path) -> dict[str, Any]:
    records: dict[str, Any] = {}
    with path.open("r", encoding="utf-8") as file:
        for line in file:
            if not line.strip():
                continue
            record = json.loads(line)
            records[record["id"]] = record.get("prediction")
    return records


def time_to_minutes(value: Any) -> int | None:
    if not isinstance(value, str) or ":" not in value:
        return None
    hour, minute = value.split(":", 1)
    try:
        return int(hour) * 60 + int(minute)
    except ValueError:
        return None


def time_match(gold: dict[str, Any], pred: dict[str, Any]) -> bool:
    if gold.get("timeLimitEnabled") is False:
        return pred.get("timeLimitEnabled") is False
    if pred.get("timeLimitEnabled") is not True:
        return False
    gold_start = time_to_minutes(gold.get("startTime"))
    gold_end = time_to_minutes(gold.get("endTime"))
    pred_start = time_to_minutes(pred.get("startTime"))
    pred_end = time_to_minutes(pred.get("endTime"))
    if None in {gold_start, gold_end, pred_start, pred_end}:
        return False
    return abs(gold_start - pred_start) <= 60 and abs(gold_end - pred_end) <= 60


def evaluate(gold_records: dict[str, dict[str, Any]], predictions: dict[str, Any], label: str) -> Metrics:
    metrics = Metrics(total=len(gold_records))

    for record_id, record in gold_records.items():
        gold = record["gold"]
        raw_prediction = predictions.get(record_id)
        pred = parse_json_maybe(raw_prediction)
        if pred is None:
            metrics.errors.append(f"{label}: {record_id} invalid JSON")
            continue

        metrics.json_valid += 1
        if is_schema_valid(pred):
            metrics.schema_valid += 1
        else:
            metrics.errors.append(f"{label}: {record_id} schema invalid")

        gold_suggest = gold.get("shouldSuggest") is True
        pred_suggest = pred.get("shouldSuggest") is True
        if gold_suggest == pred_suggest:
            metrics.should_suggest_correct += 1

        if record.get("scenario") == "conflict":
            metrics.conflict_count += 1
            if pred.get("shouldSuggest") is False:
                metrics.conflict_avoided += 1

        if not gold_suggest:
            if pred.get("shouldSuggest") is False:
                metrics.exact_match += 1
            continue

        if not pred_suggest:
            continue

        if pred.get("metricType") == gold.get("metricType"):
            metrics.metric_correct += 1
        if pred.get("actionType") == gold.get("actionType") and pred.get("actionValue") == gold.get("actionValue"):
            metrics.action_correct += 1
        if all(pred.get(field_name) == gold.get(field_name) for field_name in CORE_FIELDS):
            if pred.get("operatorType") == gold.get("operatorType"):
                metrics.exact_match += 1

        gold_threshold = gold.get("thresholdMin")
        pred_threshold = pred.get("thresholdMin")
        if isinstance(gold_threshold, (int, float)) and isinstance(pred_threshold, (int, float)):
            metrics.threshold_count += 1
            abs_error = abs(float(pred_threshold) - float(gold_threshold))
            metrics.threshold_abs_error_total += abs_error
            tolerance = max(abs(float(gold_threshold)) * 0.1, 1.0)
            if abs_error <= tolerance:
                metrics.threshold_within_10pct += 1

        metrics.time_window_count += 1
        if time_match(gold, pred):
            metrics.time_window_correct += 1

    return metrics


def fmt_pct(value: float) -> str:
    return f"{value:.1f}%"


def metric_rows(metrics: Metrics) -> list[tuple[str, str]]:
    return [
        ("JSON validity", fmt_pct(metrics.pct(metrics.json_valid))),
        ("Schema compliance", fmt_pct(metrics.pct(metrics.schema_valid))),
        ("shouldSuggest accuracy", fmt_pct(metrics.pct(metrics.should_suggest_correct))),
        ("Strategy exact match", fmt_pct(metrics.pct(metrics.exact_match))),
        ("Metric accuracy", fmt_pct(metrics.pct(metrics.metric_correct, metrics.threshold_count))),
        ("Action accuracy", fmt_pct(metrics.pct(metrics.action_correct, metrics.threshold_count))),
        ("Threshold MAE", f"{metrics.threshold_mae:.2f}"),
        ("Threshold within 10%", fmt_pct(metrics.pct(metrics.threshold_within_10pct, metrics.threshold_count))),
        ("Time window accuracy", fmt_pct(metrics.pct(metrics.time_window_correct, metrics.time_window_count))),
        ("Conflict avoidance", fmt_pct(metrics.pct(metrics.conflict_avoided, metrics.conflict_count))),
    ]


def write_report(path: Path, baseline: Metrics, finetuned: Metrics) -> None:
    baseline_rows = dict(metric_rows(baseline))
    finetuned_rows = dict(metric_rows(finetuned))
    lines = [
        "# PlantCloud Strategy Model Evaluation",
        "",
        "| Metric | Baseline | Fine-tuned | Delta |",
        "| --- | ---: | ---: | ---: |",
    ]

    for name in baseline_rows:
        base_value = baseline_rows[name]
        tuned_value = finetuned_rows[name]
        delta = "-"
        if name != "Threshold MAE":
            try:
                delta_value = float(tuned_value.rstrip("%")) - float(base_value.rstrip("%"))
                delta = f"{delta_value:+.1f} pp"
            except ValueError:
                delta = "-"
        else:
            delta = f"{float(tuned_value) - float(base_value):+.2f}"
        lines.append(f"| {name} | {base_value} | {tuned_value} | {delta} |")

    lines.extend(
        [
            "",
            "## Notes",
            "",
            "- Baseline means the model selected by the current RAGFlow Agent before fine-tuning.",
            "- Fine-tuned means the strategy model after LoRA/SFT fine-tuning.",
            "- Use the same gold test set for both runs.",
            "",
        ]
    )

    if baseline.errors or finetuned.errors:
        lines.extend(["## Parse Or Schema Issues", ""])
        for item in baseline.errors[:20] + finetuned.errors[:20]:
            lines.append(f"- {item}")
        if len(baseline.errors) + len(finetuned.errors) > 40:
            lines.append("- Additional issues omitted.")
        lines.append("")

    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text("\n".join(lines), encoding="utf-8")


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--gold", required=True)
    parser.add_argument("--baseline", required=True)
    parser.add_argument("--finetuned", required=True)
    parser.add_argument("--out", required=True)
    args = parser.parse_args()

    gold_records = load_gold(Path(args.gold))
    baseline = evaluate(gold_records, load_predictions(Path(args.baseline)), "baseline")
    finetuned = evaluate(gold_records, load_predictions(Path(args.finetuned)), "finetuned")
    write_report(Path(args.out), baseline, finetuned)

    print(f"wrote report -> {args.out}")


if __name__ == "__main__":
    main()
