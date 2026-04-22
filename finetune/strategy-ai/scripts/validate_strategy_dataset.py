"""Validate PlantCloud strategy fine-tuning JSONL files."""

from __future__ import annotations

import json
import re
import sys
from pathlib import Path
from typing import Any


REQUIRED_RECORD_KEYS = {"id", "split", "plant", "scenario", "system", "user", "assistant"}
TRUE_REQUIRED_KEYS = {
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
METRIC_TYPES = {"LIGHT", "TEMPERATURE", "HUMIDITY"}
OPERATOR_TYPES = {"LT", "GT", "EQ"}
ACTION_TYPES = {"AUTO_LIGHT", "AUTO_FAN", "NOTIFY_USER"}
ACTION_VALUES = {"ON", "OFF", "LOW", "HIGH", "INFO", "WARNING", "DANGER"}
TIME_RE = re.compile(r"^([01][0-9]|2[0-3]):[0-5][0-9]$")


def fail(message: str) -> None:
    raise ValueError(message)


def validate_time(value: Any, key: str) -> None:
    if value is None:
        return
    if not isinstance(value, str) or not TIME_RE.match(value):
        fail(f"{key} must be HH:mm or null, got {value!r}")


def validate_proposal(proposal: dict[str, Any], record_id: str) -> None:
    if set(proposal.keys()) == {"shouldSuggest"} and proposal["shouldSuggest"] is False:
        return

    if proposal.get("shouldSuggest") is not True:
        fail(f"{record_id}: shouldSuggest must be true or sole false")

    missing = TRUE_REQUIRED_KEYS - set(proposal.keys())
    if missing:
        fail(f"{record_id}: proposal missing keys: {sorted(missing)}")

    if proposal["metricType"] not in METRIC_TYPES:
        fail(f"{record_id}: invalid metricType {proposal['metricType']!r}")
    if proposal["operatorType"] not in OPERATOR_TYPES:
        fail(f"{record_id}: invalid operatorType {proposal['operatorType']!r}")
    if proposal["actionType"] not in ACTION_TYPES:
        fail(f"{record_id}: invalid actionType {proposal['actionType']!r}")
    if proposal["actionValue"] not in ACTION_VALUES:
        fail(f"{record_id}: invalid actionValue {proposal['actionValue']!r}")
    if not isinstance(proposal["thresholdMin"], (int, float)):
        fail(f"{record_id}: thresholdMin must be numeric")
    if not isinstance(proposal["timeLimitEnabled"], bool):
        fail(f"{record_id}: timeLimitEnabled must be boolean")

    validate_time(proposal["startTime"], "startTime")
    validate_time(proposal["endTime"], "endTime")

    if proposal["timeLimitEnabled"]:
        if proposal["startTime"] is None or proposal["endTime"] is None:
            fail(f"{record_id}: time-limited strategy requires startTime/endTime")
    else:
        if proposal["startTime"] is not None or proposal["endTime"] is not None:
            fail(f"{record_id}: non-time-limited strategy must use null times")


def validate_record(record: dict[str, Any], line_no: int) -> None:
    missing = REQUIRED_RECORD_KEYS - set(record.keys())
    if missing:
        fail(f"line {line_no}: record missing keys: {sorted(missing)}")

    for key in REQUIRED_RECORD_KEYS:
        if not isinstance(record[key], str):
            fail(f"line {line_no}: {key} must be string")

    try:
        proposal = json.loads(record["assistant"])
    except json.JSONDecodeError as exc:
        raise ValueError(f"line {line_no}: assistant is not valid JSON: {exc}") from exc

    if not isinstance(proposal, dict):
        fail(f"line {line_no}: assistant JSON must be object")
    validate_proposal(proposal, record["id"])


def validate_file(path: Path) -> tuple[int, int]:
    total = 0
    suggest_count = 0
    with path.open("r", encoding="utf-8") as file:
        for line_no, line in enumerate(file, start=1):
            if not line.strip():
                continue
            total += 1
            record = json.loads(line)
            validate_record(record, line_no)
            proposal = json.loads(record["assistant"])
            if proposal.get("shouldSuggest") is True:
                suggest_count += 1
    return total, suggest_count


def main() -> None:
    if len(sys.argv) < 2:
        print("Usage: python validate_strategy_dataset.py <dataset.jsonl> [more.jsonl ...]")
        raise SystemExit(2)

    for arg in sys.argv[1:]:
        path = Path(arg)
        total, suggest_count = validate_file(path)
        print(f"ok: {path} ({total} records, {suggest_count} suggestions, {total - suggest_count} no-suggest)")


if __name__ == "__main__":
    main()
