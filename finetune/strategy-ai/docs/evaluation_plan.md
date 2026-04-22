# Strategy Model Evaluation Plan

This document defines how to compare the PlantCloud strategy model before and after fine-tuning.

## Compared Runs

Use the same test set for both models:

- **Baseline**: the current RAGFlow Agent selected model before fine-tuning, such as `deepseek-chat`.
- **Fine-tuned**: the local or hosted strategy model after LoRA/SFT fine-tuning.

Each run should produce one prediction JSON object for each test sample.

## Evaluation Files

Gold labels:

```text
finetune/strategy-ai/data/generated/test.jsonl
```

Prediction files:

```text
finetune/strategy-ai/eval/predictions/baseline_predictions.jsonl
finetune/strategy-ai/eval/predictions/finetuned_predictions.jsonl
```

Prediction format:

```json
{
  "id": "test-phalaenopsis-light_low-012",
  "prediction": "{\"shouldSuggest\":true,...}"
}
```

`prediction` may be either a JSON string or a JSON object.

## Metrics

| Metric | Meaning |
| --- | --- |
| JSON validity | Whether the model output can be parsed as JSON. |
| Schema compliance | Whether the parsed output follows the PlantCloud strategy schema. |
| shouldSuggest accuracy | Whether the model correctly decides to suggest or not. |
| Strategy exact match | Whether core fields match gold: metric, operator, action, value, time flag. |
| Metric accuracy | Whether `metricType` matches gold. |
| Action accuracy | Whether `actionType` and `actionValue` match gold. |
| Threshold MAE | Mean absolute error of `thresholdMin` on suggested strategies. |
| Threshold within 10% | Whether suggested threshold differs from gold by at most 10%. |
| Time window accuracy | Whether time fields match gold or are within one hour. |
| Conflict avoidance | On conflict samples, whether model outputs `shouldSuggest=false`. |

## Suggested Target

| Metric | Target |
| --- | ---: |
| JSON validity | >= 98% |
| Schema compliance | >= 95% |
| shouldSuggest accuracy | >= 90% |
| Strategy exact match | >= 85% |
| Conflict avoidance | >= 95% |
| Threshold within 10% | >= 90% |
| Time window accuracy | >= 90% |

## Workflow

1. Run baseline model on `test.jsonl`.
2. Save outputs to `baseline_predictions.jsonl`.
3. Run fine-tuned model on the same test set.
4. Save outputs to `finetuned_predictions.jsonl`.
5. Compare:

```bash
python finetune/strategy-ai/scripts/evaluate_strategy_predictions.py \
  --gold finetune/strategy-ai/data/generated/test.jsonl \
  --baseline finetune/strategy-ai/eval/predictions/baseline_predictions.jsonl \
  --finetuned finetune/strategy-ai/eval/predictions/finetuned_predictions.jsonl \
  --out finetune/strategy-ai/eval/reports/strategy_eval_report.md
```

The report can be used in your paper, defense slides, or project README.
