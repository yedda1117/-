"""Run the current RAGFlow Agent on the PlantCloud strategy test set.

This script creates a real baseline prediction file by sending each test sample
to the configured RAGFlow Agent endpoint.

It uses only Python standard-library modules.
"""

from __future__ import annotations

import argparse
import json
import os
import time
import urllib.error
import urllib.request
from pathlib import Path
from typing import Any


ROOT = Path(__file__).resolve().parents[1]
REPO_ROOT = ROOT.parents[1]

DEFAULT_INPUT = ROOT / "data" / "generated" / "test.jsonl"
DEFAULT_OUTPUT = ROOT / "eval" / "predictions" / "baseline_predictions.jsonl"
DEFAULT_ENV = REPO_ROOT / "fronted" / ".env.local"

PROMPT_RULES = """你是 PlantCloud 策略分析 Agent。请根据用户问题和植物上下文，判断是否需要新增或调整自动化策略。
只输出 JSON，不要输出 Markdown，不要输出解释文字。

JSON 字段：
shouldSuggest, detected, strategyName, metricType, operatorType, thresholdMin, actionType, actionValue, timeLimitEnabled, startTime, endTime, reason。

字段约束：
- metricType 只能是 LIGHT/TEMPERATURE/HUMIDITY
- operatorType 只能是 LT/GT/EQ
- actionType 只能是 AUTO_LIGHT/AUTO_FAN/NOTIFY_USER
- AUTO_LIGHT 的 actionValue 只能是 ON/OFF
- AUTO_FAN 的 actionValue 只能是 LOW/HIGH
- NOTIFY_USER 的 actionValue 只能是 INFO/WARNING/DANGER
- timeLimitEnabled=true 时 startTime/endTime 必须是 HH:mm
- 不需要时间限制时 timeLimitEnabled=false, startTime=null, endTime=null

业务规则：
- 光照不足通常建议 AUTO_LIGHT + ON
- 温度过高通常建议 AUTO_FAN + HIGH
- 湿度偏低且没有浇水设备时建议 NOTIFY_USER + INFO
- 如果已有启用策略已经覆盖同一植物、同一指标、同一动作和相近阈值，输出 {"shouldSuggest":false}
- 如果不需要新增策略，输出 {"shouldSuggest":false}
"""


def load_env(path: Path) -> None:
    if not path.exists():
        return
    for raw_line in path.read_text(encoding="utf-8").splitlines():
        line = raw_line.strip()
        if not line or line.startswith("#") or "=" not in line:
            continue
        key, value = line.split("=", 1)
        key = key.strip()
        value = value.strip().strip('"').strip("'")
        os.environ.setdefault(key, value)


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


def build_endpoint() -> str:
    agent_url = os.environ.get("RAGFLOW_AGENT_URL")
    if agent_url:
        return agent_url

    base_url = os.environ.get("RAGFLOW_BASE_URL")
    agent_id = os.environ.get("RAGFLOW_AGENT_ID")
    if not base_url or not agent_id:
        raise RuntimeError("Missing RAGFLOW_AGENT_URL or RAGFLOW_BASE_URL + RAGFLOW_AGENT_ID")
    return f"{base_url.rstrip('/')}/api/v1/agents_openai/{agent_id}/chat/completions"


def build_prompt(record: dict[str, Any]) -> str:
    return "\n\n".join(
        [
            PROMPT_RULES,
            "测试样本：",
            record["user"],
        ]
    )


def call_ragflow(endpoint: str, api_key: str, prompt: str, timeout: int) -> str:
    body = json.dumps(
        {
            "model": "ragflow",
            "messages": [{"role": "user", "content": prompt}],
            "stream": False,
        },
        ensure_ascii=False,
    ).encode("utf-8")

    request = urllib.request.Request(
        endpoint,
        data=body,
        headers={
            "Content-Type": "application/json",
            "Authorization": f"Bearer {api_key}",
        },
        method="POST",
    )

    with urllib.request.urlopen(request, timeout=timeout) as response:
        payload = json.loads(response.read().decode("utf-8"))

    return payload.get("choices", [{}])[0].get("message", {}).get("content", "")


def call_with_retries(
    endpoint: str,
    api_key: str,
    prompt: str,
    timeout: int,
    retries: int,
    retry_sleep: float,
) -> str:
    last_error: Exception | None = None
    for attempt in range(1, retries + 2):
        try:
            return call_ragflow(endpoint, api_key, prompt, timeout)
        except urllib.error.HTTPError:
            raise
        except (TimeoutError, urllib.error.URLError, OSError) as exc:
            last_error = exc
            if attempt > retries:
                break
            print(f"  retry {attempt}/{retries} after {type(exc).__name__}: {exc}")
            time.sleep(retry_sleep)
    raise RuntimeError(f"RAGFlow request failed after {retries + 1} attempts: {last_error}") from last_error


def append_prediction(path: Path, record: dict[str, Any]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("a", encoding="utf-8", newline="\n") as file:
        file.write(json.dumps(record, ensure_ascii=False, separators=(",", ":")) + "\n")


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--input", default=str(DEFAULT_INPUT))
    parser.add_argument("--out", default=str(DEFAULT_OUTPUT))
    parser.add_argument("--env", default=str(DEFAULT_ENV))
    parser.add_argument("--limit", type=int, default=0, help="Limit number of samples, 0 means all")
    parser.add_argument("--sleep", type=float, default=0.3, help="Seconds to sleep between requests")
    parser.add_argument("--timeout", type=int, default=120)
    parser.add_argument("--retries", type=int, default=2)
    parser.add_argument("--retry-sleep", type=float, default=5.0)
    parser.add_argument("--keep-going", action="store_true", help="Write an error row and continue after retries fail")
    parser.add_argument("--overwrite", action="store_true")
    args = parser.parse_args()

    load_env(Path(args.env))

    api_key = os.environ.get("RAGFLOW_API_KEY")
    if not api_key:
        raise RuntimeError("Missing RAGFLOW_API_KEY")
    endpoint = build_endpoint()

    input_path = Path(args.input)
    output_path = Path(args.out)
    records = load_jsonl(input_path)
    if args.limit > 0:
        records = records[: args.limit]

    if args.overwrite and output_path.exists():
        output_path.unlink()

    existing = load_existing_predictions(output_path)
    print(f"endpoint: {endpoint}")
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
                endpoint,
                api_key,
                prompt,
                args.timeout,
                args.retries,
                args.retry_sleep,
            )
        except urllib.error.HTTPError as exc:
            body = exc.read().decode("utf-8", errors="replace")
            raise RuntimeError(f"RAGFlow HTTP {exc.code}: {body}") from exc
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
