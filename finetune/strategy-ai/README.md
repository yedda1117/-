# PlantCloud Strategy AI Fine-Tuning

This folder is isolated from the PlantCloud product code. It contains a fine-tuning scaffold for the strategy-generation model used by the RAGFlow Agent.

## Goal

Train or evaluate a model that reads PlantCloud plant context and returns one strict JSON strategy proposal:

```json
{
  "shouldSuggest": true,
  "detected": "蝴蝶兰当前光照不足",
  "strategyName": "蝴蝶兰光照不足自动补光",
  "metricType": "LIGHT",
  "operatorType": "LT",
  "thresholdMin": 800,
  "actionType": "AUTO_LIGHT",
  "actionValue": "ON",
  "timeLimitEnabled": true,
  "startTime": "08:00",
  "endTime": "20:00",
  "reason": "当前光照低于蝴蝶兰建议范围，且没有已有策略覆盖该风险。"
}
```

## Recommended Model

Primary: `Qwen2.5-7B-Instruct`

Why:

- Strong Chinese instruction-following ability.
- 7B is practical for local LoRA fine-tuning and deployment.
- Good ecosystem support in LLaMA-Factory, Ollama, Xinference, and vLLM.
- Enough capacity for structured JSON policy generation without the cost of 14B.

Alternative:

- `Qwen2.5-14B-Instruct` if GPU memory is sufficient.
- `DeepSeek-R1-Distill-Qwen-7B` if reasoning quality matters more than response latency.

## Folder Layout

```text
finetune/strategy-ai/
├─ configs/
│  ├─ dataset_info.json
│  └─ llamafactory_qwen2_5_7b_lora.yaml
├─ data/
│  ├─ examples/
│  │  └─ strategy_samples.jsonl
│  └─ generated/
├─ rules/
│  └─ plant_strategy_rules.md
├─ schemas/
│  └─ strategy_proposal.schema.json
└─ scripts/
   ├─ convert_to_llamafactory.py
   ├─ generate_strategy_dataset.py
   └─ validate_strategy_dataset.py
```

## Dataset Plan

Target distribution:

| Split | Phalaenopsis | Succulents | Total |
| --- | ---: | ---: | ---: |
| Train | 100 | 100 | 200 |
| Validation | 20 | 20 | 40 |
| Test | 15 | 15 | 30 |

Training scenarios per plant:

| Scenario | Count |
| --- | ---: |
| Temperature abnormal | 30 |
| Humidity abnormal | 25 |
| Light insufficient | 25 |
| Existing strategy conflict | 20 |

## Generate Data

From repo root:

```bash
python finetune/strategy-ai/scripts/generate_strategy_dataset.py
```

This writes:

```text
finetune/strategy-ai/data/generated/train.jsonl
finetune/strategy-ai/data/generated/validation.jsonl
finetune/strategy-ai/data/generated/test.jsonl
```

## Validate Data

```bash
python finetune/strategy-ai/scripts/validate_strategy_dataset.py finetune/strategy-ai/data/generated/train.jsonl
python finetune/strategy-ai/scripts/validate_strategy_dataset.py finetune/strategy-ai/data/generated/validation.jsonl
python finetune/strategy-ai/scripts/validate_strategy_dataset.py finetune/strategy-ai/data/generated/test.jsonl
```

## Convert To LLaMA-Factory ShareGPT Format

```bash
python finetune/strategy-ai/scripts/convert_to_llamafactory.py
```

This writes:

```text
finetune/strategy-ai/data/llamafactory/plantcloud_strategy_train.json
finetune/strategy-ai/data/llamafactory/plantcloud_strategy_validation.json
finetune/strategy-ai/data/llamafactory/plantcloud_strategy_test.json
```

Copy or symlink these files into your LLaMA-Factory `data/` directory, and copy `configs/dataset_info.json` entries into LLaMA-Factory's `data/dataset_info.json`.

## Training Command

Example with LLaMA-Factory:

```bash
llamafactory-cli train finetune/strategy-ai/configs/llamafactory_qwen2_5_7b_lora.yaml
```

Adjust paths in the YAML to your local LLaMA-Factory environment.

## Deployment Back To RAGFlow

After LoRA fine-tuning:

1. Export or merge the model.
2. Serve it through Ollama, Xinference, vLLM, or another OpenAI-compatible endpoint.
3. Add it in RAGFlow under Model Providers.
4. Set the RAGFlow Agent LLM to the fine-tuned strategy model.

RAGFlow remains the Agent/RAG orchestration layer. The fine-tuned model is the LLM selected by the Agent.

## Before/After Metrics

The evaluation scaffold compares the current RAGFlow Agent model against the fine-tuned strategy model on the same test set.

Gold test set:

```text
finetune/strategy-ai/data/generated/test.jsonl
```

Prediction files:

```text
finetune/strategy-ai/eval/predictions/baseline_predictions.jsonl
finetune/strategy-ai/eval/predictions/finetuned_predictions.jsonl
```

To generate a real baseline with the current RAGFlow Agent:

```bash
python finetune/strategy-ai/scripts/run_ragflow_agent_baseline.py --overwrite
```

By default, the script:

- Reads environment variables from `fronted/.env.local`.
- Reads test samples from `finetune/strategy-ai/data/generated/test.jsonl`.
- Writes predictions to `finetune/strategy-ai/eval/predictions/baseline_predictions.jsonl`.
- Uses `RAGFLOW_AGENT_URL` if present, otherwise uses `RAGFLOW_BASE_URL + RAGFLOW_AGENT_ID`.

For a quick smoke test with only 3 samples:

```bash
python finetune/strategy-ai/scripts/run_ragflow_agent_baseline.py --overwrite --limit 3
```

For a demo report, create oracle fine-tuned predictions:

```bash
python finetune/strategy-ai/scripts/create_oracle_predictions.py
```

Then compare baseline and fine-tuned outputs:

```bash
python finetune/strategy-ai/scripts/evaluate_strategy_predictions.py \
  --gold finetune/strategy-ai/data/generated/test.jsonl \
  --baseline finetune/strategy-ai/eval/predictions/baseline_predictions.jsonl \
  --finetuned finetune/strategy-ai/eval/predictions/finetuned_predictions.jsonl \
  --out finetune/strategy-ai/eval/reports/strategy_eval_report.md
```

Report output:

```text
finetune/strategy-ai/eval/reports/strategy_eval_report.md
```

Important: the included `finetuned_predictions.jsonl` can be generated from gold labels for demonstration. Replace it with actual fine-tuned model outputs when you run a real experiment.
