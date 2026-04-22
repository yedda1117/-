"""Run a local Ollama model on the PlantCloud strategy test set.

This is a faster baseline path than going through RAGFlow Agent. It evaluates
the base model itself, which is useful before LoRA/SFT fine-tuning.

It uses only Python standard-library modules.
"""

from __future__ import annotations

import argparse
import json
import time
import urllib.error
import urllib.request
from pathlib import Path
from typing import Any


ROOT = Path(__file__).resolve().parents[1]

DEFAULT_INPUT = ROOT / "data" / "generated" / "test.jsonl"
DEFAULT_OUTPUT = ROOT / "eval" / "predictions" / "baseline_predictions.jsonl"
DEFAULT_OLLAMA_URL = "http://localhost:11434/api/generate"
DEFAULT_MODEL = "qwen2.5:7b"

PROMPT_RULES = """你是 PlantCloud 策略分析模型。请根据用户问题和植物上下文，判断是否需要新增或调整自动化策略。
只输出 JSON，不要输出 Markdown，不要输出解释文字。

如果需要新增策略，输出完整 JSON：
{
  "shouldSuggest": true,
  "detected": "...",
  "strategyName": "...",
  "metricType": "LIGHT|TEMPERATURE|HUMIDITY",
  "operatorType": "LT|GT|EQ",
  "thresholdMin": 123,
  "actionType": "AUTO_LIGHT|AUTO_FAN|NOTIFY_USER",
  "actionValue": "ON|OFF|LOW|HIGH|INFO|WARNING|DANGER",
  "timeLimitEnabled": true,
  "startTime": "HH:mm",
  "endTime": "HH:mm",
  "reason": "..."
}

如果不需要新增策略，输出：
{"shouldSuggest": false}

业务规则：
- 光照不足通常建议 AUTO_LIGHT + ON
- 温度过高通常建议 AUTO_FAN + HIGH
- 湿度偏低且没有浇水设备时建议 NOTIFY_USER + INFO
- 如果已有启用策略已经覆盖同一植物、同一指标、同一动作和相近阈值，输出 {"shouldSuggest": false}
"""


def load_jsonl(path: Path) -> list[dict[str, Any]]:
    records: list[dict[str, Any]] = []
    with path.open("r", encoding="utf-8") as file:
        for line in file:
            if line.strip():
                records.append(json.loads(line))
    return records


def load_existing_predictions(path: Path) -> dict[str, dict[str, Any]]:
    if not path.exists():
        return {}
    records: dict[str, dict[str, Any]] = {}
    with path.open("r", encoding="utf-8") as file:
        for line in file:
            if not line.strip():
                continue
            record = json.loads(line)
            records[record["id"]] = record
    return records


def build_prompt(record: dict[str, Any]) -> str:
    return "\n\n".join(
        [
            PROMPT_RULES,
            "测试样本：",
            record["user"],
        ]
    )


def call_ollama(
    url: str,
    model: str,
    prompt: str,
    timeout: int,
    keep_alive: str,
    num_predict: int,
) -> str:
    body = json.dumps(
        {
            "model": model,
            "prompt": prompt,
            "stream": False,
            "format": "json",
            "keep_alive": keep_alive,
            "options": {
                "temperature": 0,
                "num_ctx": 4096,
                "num_predict": num_predict,
            },
        },
        ensure_ascii=False,
    ).encode("utf-8")

    request = urllib.request.Request(
        url,
        data=body,
        headers={"Content-Type": "application/json"},
        method="POST",
    )

    with urllib.request.urlopen(request, timeout=timeout) as response:
        payload = json.loads(response.read().decode("utf-8"))

    return payload.get("response", "").strip()


def call_with_retries(
    url: str,
    model: str,
    prompt: str,
    timeout: int,
    retries: int,
    retry_sleep: float,
    keep_alive: str,
    num_predict: int,
) -> str:
    last_error: Exception | None = None
    for attempt in range(1, retries + 2):
        try:
            return call_ollama(url, model, prompt, timeout, keep_alive, num_predict)
        except (TimeoutError, urllib.error.URLError, OSError) as exc:
            last_error = exc
            if attempt > retries:
                break
            print(f"  retry {attempt}/{retries} after {type(exc).__name__}: {exc}")
            time.sleep(retry_sleep)
    raise RuntimeError(f"Ollama request failed after {retries + 1} attempts: {last_error}") from last_error


def append_prediction(path: Path, record: dict[str, Any]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("a", encoding="utf-8", newline="\n") as file:
        file.write(json.dumps(record, ensure_ascii=False, separators=(",", ":")) + "\n")


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--input", default=str(DEFAULT_INPUT))
    parser.add_argument("--out", default=str(DEFAULT_OUTPUT))
    parser.add_argument("--url", default=DEFAULT_OLLAMA_URL)
    parser.add_argument("--model", default=DEFAULT_MODEL)
    parser.add_argument("--limit", type=int, default=0, help="Limit number of samples, 0 means all")
    parser.add_argument("--sleep", type=float, default=0.2, help="Seconds to sleep between requests")
    parser.add_argument("--timeout", type=int, default=300)
    parser.add_argument("--retries", type=int, default=2)
    parser.add_argument("--retry-sleep", type=float, default=5.0)
    parser.add_argument("--keep-alive", default="30m")
    parser.add_argument("--num-predict", type=int, default=256)
    parser.add_argument("--keep-going", action="store_true", help="Write an error row and continue after retries fail")
    parser.add_argument("--overwrite", action="store_true")
    args = parser.parse_args()

    input_path = Path(args.input)
    output_path = Path(args.out)
    records = load_jsonl(input_path)
    if args.limit > 0:
        records = records[: args.limit]

    if args.overwrite and output_path.exists():
        output_path.unlink()

    existing = load_existing_predictions(output_path)
    print(f"ollama:   {args.url}")
    print(f"model:    {args.model}")
    print(f"input:    {input_path}")
    print(f"output:   {output_path}")
    print(f"samples:  {len(records)}")

    for index, record in enumerate(records, start=1):
        record_id = record["id"]
        if record_id in existing:
            print(f"[{index}/{len(records)}] skip existing {record_id}")
            continue

        prompt = build_prompt(record)
        print(f"[{index}/{len(records)}] request {record_id}")
        try:
            prediction = call_with_retries(
                args.url,
                args.model,
                prompt,
                args.timeout,
                args.retries,
                args.retry_sleep,
                args.keep_alive,
                args.num_predict,
            )
        except RuntimeError as exc:
            if not args.keep_going:
                raise
            append_prediction(
                output_path,
                {
                    "id": record_id,
                    "prediction": "",
                    "error": str(exc),
                },
            )
            print(f"  failed and continued: {exc}")
            time.sleep(args.sleep)
            continue

        append_prediction(
            output_path,
            {
                "id": record_id,
                "prediction": prediction,
            },
        )
        time.sleep(args.sleep)

    print("done")


if __name__ == "__main__":
    main()
