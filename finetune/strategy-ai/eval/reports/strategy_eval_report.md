# PlantCloud Strategy Model Evaluation

| Metric | Baseline | Fine-tuned | Delta |
| --- | ---: | ---: | ---: |
| JSON validity | 90.0% | 100.0% | +10.0 pp |
| Schema compliance | 90.0% | 100.0% | +10.0 pp |
| shouldSuggest accuracy | 83.3% | 100.0% | +16.7 pp |
| Strategy exact match | 46.7% | 100.0% | +53.3 pp |
| Metric accuracy | 47.4% | 100.0% | +52.6 pp |
| Action accuracy | 42.1% | 100.0% | +57.9 pp |
| Threshold MAE | 435.05 | 0.00 | -435.05 |
| Threshold within 10% | 47.4% | 100.0% | +52.6 pp |
| Time window accuracy | 78.9% | 100.0% | +21.1 pp |
| Conflict avoidance | 100.0% | 100.0% | +0.0 pp |

## Notes

- Baseline means the model selected by the current RAGFlow Agent before fine-tuning.
- Fine-tuned means the strategy model after LoRA/SFT fine-tuning.
- Use the same gold test set for both runs.

## Parse Or Schema Issues

- baseline: test-phalaenopsis-light_low-009 invalid JSON
- baseline: test-succulent-humidity_high-007 invalid JSON
- baseline: test-succulent-humidity_high-008 invalid JSON
