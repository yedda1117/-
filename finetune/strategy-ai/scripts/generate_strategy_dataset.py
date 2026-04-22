"""Generate PlantCloud strategy fine-tuning datasets.

The generated records are deterministic and intended as a starter supervised
fine-tuning set. Replace or enrich them with expert-reviewed production data
before training a production model.
"""

from __future__ import annotations

import json
from dataclasses import dataclass
from pathlib import Path
from typing import Any


ROOT = Path(__file__).resolve().parents[1]
OUT_DIR = ROOT / "data" / "generated"

SYSTEM_PROMPT = "你是 PlantCloud 策略分析模型。只输出 JSON，不要输出 Markdown 或解释。"


@dataclass(frozen=True)
class PlantProfile:
    code: str
    name: str
    temp_low: float
    temp_high: float
    humidity_low: float
    humidity_high: float
    light_low: float
    light_high: float
    light_start: str
    light_end: str


PLANTS = {
    "phalaenopsis": PlantProfile(
        code="phalaenopsis",
        name="蝴蝶兰",
        temp_low=18,
        temp_high=28,
        humidity_low=60,
        humidity_high=80,
        light_low=800,
        light_high=1500,
        light_start="08:00",
        light_end="20:00",
    ),
    "succulent": PlantProfile(
        code="succulent",
        name="多肉植物",
        temp_low=15,
        temp_high=25,
        humidity_low=30,
        humidity_high=50,
        light_low=1000,
        light_high=2000,
        light_start="08:00",
        light_end="18:00",
    ),
}


QUESTION_TEMPLATES = [
    "当前策略是否需要调整？",
    "{plant}现在要不要更改策略？",
    "是否需要新增一条自动化策略？",
    "{plant}的环境策略要不要优化？",
    "帮我判断一下当前策略是否合理。",
]


def dump_json(value: Any) -> str:
    return json.dumps(value, ensure_ascii=False, separators=(",", ":"))


def make_context(
    plant: PlantProfile,
    temperature: float,
    humidity: float,
    light: float,
    current_strategies: list[dict[str, Any]] | None = None,
) -> dict[str, Any]:
    return {
        "plantName": plant.name,
        "temperature": round(temperature, 1),
        "humidity": round(humidity, 1),
        "light": round(light, 1),
        "currentStrategies": current_strategies or [],
    }


def base_context_values(plant: PlantProfile, index: int) -> tuple[float, float, float]:
    temp = (plant.temp_low + plant.temp_high) / 2 + (index % 3 - 1) * 0.4
    humidity = (plant.humidity_low + plant.humidity_high) / 2 + (index % 5 - 2) * 1.0
    light = (plant.light_low + plant.light_high) / 2 + (index % 4 - 1) * 40
    return temp, humidity, light


def proposal_false() -> dict[str, Any]:
    return {"shouldSuggest": False}


def proposal_for(
    plant: PlantProfile,
    scenario: str,
    temperature: float,
    humidity: float,
    light: float,
) -> dict[str, Any]:
    if scenario == "temperature_high":
        return {
            "shouldSuggest": True,
            "detected": f"{plant.name}当前温度偏高",
            "strategyName": f"{plant.name}高温自动通风",
            "metricType": "TEMPERATURE",
            "operatorType": "GT",
            "thresholdMin": plant.temp_high,
            "actionType": "AUTO_FAN",
            "actionValue": "HIGH",
            "timeLimitEnabled": False,
            "startTime": None,
            "endTime": None,
            "reason": f"当前温度 {temperature:.1f}°C 高于{plant.name}建议上限 {plant.temp_high:.0f}°C，且没有已有策略覆盖该风险。",
        }
    if scenario == "temperature_low":
        return {
            "shouldSuggest": True,
            "detected": f"{plant.name}当前温度偏低",
            "strategyName": f"{plant.name}低温提醒策略",
            "metricType": "TEMPERATURE",
            "operatorType": "LT",
            "thresholdMin": plant.temp_low,
            "actionType": "NOTIFY_USER",
            "actionValue": "WARNING",
            "timeLimitEnabled": False,
            "startTime": None,
            "endTime": None,
            "reason": f"当前温度 {temperature:.1f}°C 低于{plant.name}建议下限 {plant.temp_low:.0f}°C，需要提醒用户保温。",
        }
    if scenario == "humidity_low":
        return {
            "shouldSuggest": True,
            "detected": f"{plant.name}当前湿度偏低",
            "strategyName": f"{plant.name}湿度偏低提醒",
            "metricType": "HUMIDITY",
            "operatorType": "LT",
            "thresholdMin": plant.humidity_low,
            "actionType": "NOTIFY_USER",
            "actionValue": "INFO",
            "timeLimitEnabled": False,
            "startTime": None,
            "endTime": None,
            "reason": f"当前湿度 {humidity:.1f}% 低于{plant.name}建议范围，系统暂无浇水设备，建议先通知用户检查环境。",
        }
    if scenario == "humidity_high":
        return {
            "shouldSuggest": True,
            "detected": f"{plant.name}当前湿度偏高",
            "strategyName": f"{plant.name}湿度偏高通风策略",
            "metricType": "HUMIDITY",
            "operatorType": "GT",
            "thresholdMin": plant.humidity_high,
            "actionType": "AUTO_FAN",
            "actionValue": "LOW",
            "timeLimitEnabled": False,
            "startTime": None,
            "endTime": None,
            "reason": f"当前湿度 {humidity:.1f}% 高于{plant.name}建议上限，建议低档通风降低湿度。",
        }
    if scenario == "light_low":
        return {
            "shouldSuggest": True,
            "detected": f"{plant.name}当前光照不足",
            "strategyName": f"{plant.name}光照不足自动补光",
            "metricType": "LIGHT",
            "operatorType": "LT",
            "thresholdMin": plant.light_low,
            "actionType": "AUTO_LIGHT",
            "actionValue": "ON",
            "timeLimitEnabled": True,
            "startTime": plant.light_start,
            "endTime": plant.light_end,
            "reason": f"当前光照 {light:.1f} lux 低于{plant.name}建议下限 {plant.light_low:.0f} lux，且没有已有策略覆盖该风险。",
        }
    raise ValueError(f"Unsupported scenario: {scenario}")


def conflict_strategy_from(proposal: dict[str, Any]) -> dict[str, Any]:
    return {
        "strategyName": proposal["strategyName"],
        "enabled": True,
        "metricType": proposal["metricType"],
        "operatorType": proposal["operatorType"],
        "thresholdMin": proposal["thresholdMin"],
        "actionType": proposal["actionType"],
        "actionValue": proposal["actionValue"],
        "timeLimitEnabled": proposal["timeLimitEnabled"],
        "startTime": proposal["startTime"],
        "endTime": proposal["endTime"],
    }


def make_record(
    split: str,
    plant: PlantProfile,
    scenario: str,
    index: int,
    force_conflict: bool = False,
) -> dict[str, Any]:
    temp, humidity, light = base_context_values(plant, index)

    if scenario == "temperature_high":
        temp = plant.temp_high + 1.5 + (index % 4) * 0.6
    elif scenario == "temperature_low":
        temp = plant.temp_low - 1.0 - (index % 3) * 0.5
    elif scenario == "humidity_low":
        humidity = plant.humidity_low - 4 - (index % 4)
    elif scenario == "humidity_high":
        humidity = plant.humidity_high + 4 + (index % 4)
    elif scenario == "light_low":
        light = plant.light_low - 120 - (index % 5) * 45

    base_proposal = proposal_for(plant, scenario, temp, humidity, light)
    strategies: list[dict[str, Any]] = []
    answer = base_proposal

    if force_conflict:
        strategies = [conflict_strategy_from(base_proposal)]
        answer = proposal_false()

    context = make_context(plant, temp, humidity, light, strategies)
    question = QUESTION_TEMPLATES[index % len(QUESTION_TEMPLATES)].format(plant=plant.name)
    user = f"用户问题：{question}\n植物上下文：{dump_json(context)}"

    return {
        "id": f"{split}-{plant.code}-{scenario}-{index:03d}",
        "split": split,
        "plant": plant.code,
        "scenario": "conflict" if force_conflict else scenario,
        "system": SYSTEM_PROMPT,
        "user": user,
        "assistant": dump_json(answer),
    }


def generate_for_plant(split: str, plant: PlantProfile, counts: dict[str, int]) -> list[dict[str, Any]]:
    records: list[dict[str, Any]] = []
    index = 0

    for scenario, count in counts.items():
        if scenario == "conflict":
            conflict_sources = ["temperature_high", "humidity_low", "light_low", "humidity_high"]
            for _ in range(count):
                source = conflict_sources[index % len(conflict_sources)]
                records.append(make_record(split, plant, source, index, force_conflict=True))
                index += 1
            continue

        for _ in range(count):
            records.append(make_record(split, plant, scenario, index))
            index += 1

    return records


def write_jsonl(path: Path, records: list[dict[str, Any]]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", encoding="utf-8", newline="\n") as file:
        for record in records:
            file.write(dump_json(record) + "\n")


def build_dataset() -> dict[str, list[dict[str, Any]]]:
    train_counts = {
        "temperature_high": 15,
        "temperature_low": 15,
        "humidity_low": 13,
        "humidity_high": 12,
        "light_low": 25,
        "conflict": 20,
    }
    validation_counts = {
        "temperature_high": 3,
        "temperature_low": 3,
        "humidity_low": 3,
        "humidity_high": 2,
        "light_low": 5,
        "conflict": 4,
    }
    test_counts = {
        "temperature_high": 3,
        "temperature_low": 2,
        "humidity_low": 2,
        "humidity_high": 2,
        "light_low": 3,
        "conflict": 3,
    }

    datasets: dict[str, list[dict[str, Any]]] = {}
    for split, counts in [
        ("train", train_counts),
        ("validation", validation_counts),
        ("test", test_counts),
    ]:
        records: list[dict[str, Any]] = []
        for plant in PLANTS.values():
            records.extend(generate_for_plant(split, plant, counts))
        datasets[split] = records
    return datasets


def main() -> None:
    datasets = build_dataset()
    for split, records in datasets.items():
        write_jsonl(OUT_DIR / f"{split}.jsonl", records)
        print(f"wrote {len(records):3d} records -> {OUT_DIR / f'{split}.jsonl'}")


if __name__ == "__main__":
    main()
