"""Convert PlantCloud JSONL records to LLaMA-Factory ShareGPT JSON."""

from __future__ import annotations

import json
from pathlib import Path
from typing import Any


ROOT = Path(__file__).resolve().parents[1]
IN_DIR = ROOT / "data" / "generated"
OUT_DIR = ROOT / "data" / "llamafactory"


def load_jsonl(path: Path) -> list[dict[str, Any]]:
    records: list[dict[str, Any]] = []
    with path.open("r", encoding="utf-8") as file:
        for line in file:
            if line.strip():
                records.append(json.loads(line))
    return records


def convert_record(record: dict[str, Any]) -> dict[str, Any]:
    return {
        "id": record["id"],
        "system": record["system"],
        "conversations": [
            {
                "from": "human",
                "value": record["user"],
            },
            {
                "from": "gpt",
                "value": record["assistant"],
            },
        ],
    }


def write_json(path: Path, records: list[dict[str, Any]]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", encoding="utf-8", newline="\n") as file:
        json.dump(records, file, ensure_ascii=False, indent=2)
        file.write("\n")


def main() -> None:
    mapping = {
        "train": "plantcloud_strategy_train.json",
        "validation": "plantcloud_strategy_validation.json",
        "test": "plantcloud_strategy_test.json",
    }
    for split, out_name in mapping.items():
        in_path = IN_DIR / f"{split}.jsonl"
        records = [convert_record(record) for record in load_jsonl(in_path)]
        out_path = OUT_DIR / out_name
        write_json(out_path, records)
        print(f"wrote {len(records):3d} records -> {out_path}")


if __name__ == "__main__":
    main()
