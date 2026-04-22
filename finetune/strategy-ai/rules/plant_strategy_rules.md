# PlantCloud Strategy Rules

These rules define the first supervised fine-tuning target for PlantCloud strategy generation. They are intentionally conservative and should be reviewed by domain experts before production use.

## Output Contract

The model must return JSON only. No Markdown. No explanation outside JSON.

Required keys:

- `shouldSuggest`: boolean
- `detected`: string
- `strategyName`: string
- `metricType`: `LIGHT | TEMPERATURE | HUMIDITY`
- `operatorType`: `LT | GT | EQ`
- `thresholdMin`: number
- `actionType`: `AUTO_LIGHT | AUTO_FAN | NOTIFY_USER`
- `actionValue`: `ON | OFF | LOW | HIGH | INFO | WARNING | DANGER`
- `timeLimitEnabled`: boolean
- `startTime`: `HH:mm` or null
- `endTime`: `HH:mm` or null
- `reason`: string

If no new strategy should be created, return:

```json
{"shouldSuggest": false}
```

## General Rules

- Do not create duplicate strategies when an enabled existing strategy already covers the same plant, metric, action, and a similar threshold.
- Prefer one high-confidence strategy over multiple vague strategies.
- If critical data is missing, return `{"shouldSuggest": false}`.
- If the user is not asking about strategy creation or strategy adjustment, return `{"shouldSuggest": false}`.
- The model proposes strategy JSON only. Backend validation still decides whether it can be saved.

## Phalaenopsis

Reference ranges:

- Day temperature: 25-28 C
- Night temperature: 18-22 C
- Humidity: 60-80%
- Light: 800-1500 lux
- Light duration: 10-12 hours per day

Suggested actions:

- Temperature above 28 C: `TEMPERATURE GT 28 -> AUTO_FAN HIGH`
- Temperature below 18 C: `TEMPERATURE LT 18 -> NOTIFY_USER WARNING`
- Humidity below 60%: `HUMIDITY LT 60 -> NOTIFY_USER INFO`
- Humidity above 80%: `HUMIDITY GT 80 -> AUTO_FAN LOW`
- Light below 800 lux: `LIGHT LT 800 -> AUTO_LIGHT ON`, usually `08:00-20:00`
- Light above 1500 lux: `LIGHT GT 1500 -> NOTIFY_USER WARNING`

## Succulents

Reference ranges:

- Temperature: 15-25 C
- Ideal temperature: about 20 C
- Humidity: 30-50%
- Light: 1000-2000 lux

Suggested actions:

- Temperature above 25 C: `TEMPERATURE GT 25 -> AUTO_FAN HIGH`
- Temperature below 15 C: `TEMPERATURE LT 15 -> NOTIFY_USER WARNING`
- Humidity above 50%: `HUMIDITY GT 50 -> AUTO_FAN LOW`
- Humidity below 30%: `HUMIDITY LT 30 -> NOTIFY_USER INFO`
- Light below 1000 lux: `LIGHT LT 1000 -> AUTO_LIGHT ON`, usually `08:00-18:00`
- Light above 2000 lux: `LIGHT GT 2000 -> NOTIFY_USER WARNING`

## Conflict Rule

An existing strategy is considered a conflict if:

- It is enabled.
- It has the same `metricType`.
- It has the same `operatorType`.
- It has the same `actionType`.
- Its threshold is close to the proposed threshold.

Threshold closeness:

- Temperature: within 2 C
- Humidity: within 5%
- Light: within 150 lux
