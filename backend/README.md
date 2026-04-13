# PlantCloud Backend

## 已创建内容

- Java 17 + Spring Boot 3.x Maven 工程
- MyBatis-Plus、Redis、MQTT、Quartz、WebSocket、MinIO 依赖
- 统一返回体与全局异常处理
- Spring Security + JWT 基础骨架
- 与现有 OpenAPI 对齐的控制器骨架
- 对应现有数据库表的实体与 Mapper 骨架
- `photo_logs` 最小增量建表 SQL

## 已对齐的接口

- `GET /monitoring/environment/current`
- `GET /monitoring/devices/status`
- `GET /visualization/history`
- `GET /visualization/calendar`
- `GET /visualization/calendar/{date}`
- `GET /visualization/strategy-logs`
- `POST /control/light`
- `POST /control/fan`
- `GET /strategies/threshold`
- `PUT /strategies/threshold`
- `GET /strategies/auto-control`
- `PUT /strategies/auto-control`
- `GET /strategies/schedule`
- `POST /strategies/schedule`
- `PUT /strategies/schedule/{scheduleId}`
- `DELETE /strategies/schedule/{scheduleId}`
- `GET /alerts/current`
- `GET /alerts/logs`
- `POST /alerts/{alertId}/acknowledge`
- `GET /companion/events`
- `POST /photos/upload`
- `GET /photos`
- `GET /photos/{date}`
- `POST /qa/ask`
- `GET /qa/history`

## 下一步建议

1. 接入真实数据库并补充 Mapper XML 或 LambdaQuery 逻辑
2. 实现 JWT 解析和当前登录用户注入
3. 实现 MQTT 订阅、消息解析、入库和策略引擎
4. 实现设备控制、回执与 WebSocket 推送
5. 接入 MinIO 与 SmartAI
