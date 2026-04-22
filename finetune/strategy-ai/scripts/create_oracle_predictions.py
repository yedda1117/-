"""Create oracle predictions from gold labels for evaluation demo purposes."""

from __future__ import annotations

import json
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
GOLD = ROOT / "data" / "generated" / "test.jsonl"
OUT = ROOT / "eval" / "predictions" / "finetuned_predictions.jsonl"


def main() -> None:
    OUT.parent.mkdir(parents=True, exist_ok=True)
    with GOLD.open("r", encoding="utf-8") as src, OUT.open("w", encoding="utf-8", newline="\n") as dst:
        for line in src:
            if not line.strip():
                continue
            record = json.loads(line)
            prediction = {
                "id": record["id"],
                "prediction": record["assistant"],
            }
            dst.write(json.dumps(prediction, ensure_ascii=False, separators=(",", ":")) + "\n")
    print(f"wrote oracle predictions -> {OUT}")


if __name__ == "__main__":
    main()
