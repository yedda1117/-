# PlantCloud

> 一套把温室、传感器、设备控制、数据可视化和 AI 种植建议连起来的智慧农业云平台。

PlantCloud 不只是一个“看数据”的面板。它更像一间会回应的数字温室：传感器持续回传环境状态，后端把数据沉淀成可追踪的植物档案，前端把温湿度、光照、设备状态和告警变成清晰的交互界面，AI 管家再根据植物上下文给出策略建议。植物负责生长，我们负责把系统做得漂亮、稳定、聪明一点。

![Java](https://img.shields.io/badge/Java-17-2f6f4e?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-4f8f63?style=flat-square)
![Next.js](https://img.shields.io/badge/Next.js-16-111111?style=flat-square)
![React](https://img.shields.io/badge/React-19-2b6cb0?style=flat-square)
![MyBatis Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.7-c2410c?style=flat-square)
![MQTT](https://img.shields.io/badge/MQTT-enabled-6b46c1?style=flat-square)

## What It Does

PlantCloud 面向真实农业设备和可视化管理场景，核心能力包括：

- **环境监测**：温度、湿度、光照、设备状态等数据接入与展示。
- **设备控制**：补光灯、风扇等设备的远程控制与状态同步。
- **植物档案**：植物列表、绑定设备、风险分析和上下文管理。
- **数据可视化**：历史曲线、日历视图、策略日志和监控总览。
- **AI 种植助手**：结合植物状态与知识库能力，提供对话式问答和策略建议。
- **智能告警**：异常状态记录、告警日志和设备事件追踪。
- **图片与识别**：图片上传、植物照片管理，以及 SmartJavaAI 相关视觉能力。
- **安全登录**：JWT 鉴权、人脸注册与人脸登录能力。

## Architecture

```text
PlantCloud
├─ fronted/              # Next.js + React 前端应用
│  ├─ app/               # 页面、路由和 API bridge
│  ├─ lib/               # 前端请求封装与业务上下文
│  └─ components/        # UI 组件
├─ backend/              # Spring Boot 后端服务
│  ├─ auth/              # 登录、鉴权、人脸认证
│  ├─ monitoring/        # 环境监测
│  ├─ device/            # 设备状态与绑定
│  ├─ visualization/     # 可视化数据接口
│  ├─ strategy/          # 策略配置与日志
│  ├─ mqtt/              # 硬件消息接入
│  ├─ photo/             # 图片上传与管理
│  ├─ qa/                # 智能问答
│  └─ config/            # 安全、跨域、服务配置
├─ device/               # 硬件端与模块资料
└─ docs/                 # 接口文档、用户故事、MQTT 文档
```

## Tech Stack

| Layer | Stack |
| --- | --- |
| Frontend | Next.js 16, React 19, TypeScript, Tailwind CSS, Radix UI, Recharts, Leaflet |
| Backend | Java 17, Spring Boot 3.3, Spring Security, WebSocket, Quartz |
| Data | MySQL, Redis, MyBatis-Plus |
| IoT | MQTT, Eclipse Paho |
| AI & Media | Ragflow API bridge, SmartJavaAI, MinIO |
| Docs | Springdoc OpenAPI / Swagger UI |

## Quick Start

### 1. Backend

```bash
cd backend
mvn spring-boot:run
```

默认服务地址：

```text
http://localhost:8080
```

Swagger UI：

```text
http://localhost:8080/swagger-ui.html
```

后端默认读取 `backend/src/main/resources/application.yml`，并启用 `dev` profile。数据库、Redis、MQTT、MinIO 等本地参数请根据 `application-dev.yml` 调整。

### 2. Frontend

```bash
cd fronted
npm install
npm run dev
```

默认前端地址：

```text
http://localhost:3000
```

## Main Pages

| Page | Purpose |
| --- | --- |
| `/home` | 植物与设备总览 |
| `/dashboard` | 环境监测与可视化大屏 |
| `/calendar` | 日历式种植记录 |
| `/chat` | AI 植物管家对话 |
| `/settings` | 设备、策略与系统设置 |
| `/login` / `/register` | 登录、注册与身份入口 |

## API Highlights

| Module | Endpoints |
| --- | --- |
| Auth | `POST /auth/login`, `POST /auth/face-register`, `POST /auth/face-login` |
| Monitoring | `GET /monitoring/environment/current`, `GET /monitoring/devices/status` |
| Devices | `GET /devices/status`, `GET /devices/infrared`, `POST /devices/bind-plant` |
| Visualization | `GET /visualization/history`, `GET /visualization/calendar`, `GET /visualization/strategy-logs` |
| Control | `POST /control/light`, `POST /control/fan` |
| Plants | `GET /plants`, `POST /plants/{id}/analyze-risk` |
| Strategies | `GET /strategies`, `POST /strategies`, `PUT /strategies/{strategyId}` |
| Photos | `POST /photos/upload`, `DELETE /photos/{date}` |
| QA | `POST /qa/ask`, `GET /qa/history` |

## Contributors

2026 年 4 月 12 日至 2026 年 4 月 19 日，我们经历了一周高密度推进：前后端、硬件、AI、页面交互、数据接入一路并行。下面是这段时间的贡献统计。

| Rank | Contributor | Commits | Additions | Deletions |
| ---: | --- | ---: | ---: | ---: |
| 1 | `yedda1117` | 30 | 21,873 | 834 |
| 2 | `civet0921` | 13 | 36,904 | 18,227 |
| 3 | `Cindy-1123` | 12 | 10,094 | 3,581 |
| 4 | `Sylvia-x5796` | 4 | 2,079 | 610 |
| 5 | `x1808843327-sys` | 1 | 0 | 3,111 |

这一周的提交曲线很有 PlantCloud 的气质：有人铺基础，有人接设备，有人调页面，有人修接口，有人把 AI 和植物上下文缝到一起。代码不是一夜长出来的，但它确实在这一周长得很快。

## Project Rhythm

- **先连接真实世界**：传感器、人体感应、设备状态、MQTT 消息是平台的根。
- **再变成可理解的数据**：历史曲线、日历、总览面板让状态不再散落。
- **然后交给策略和 AI**：阈值、策略日志、Ragflow 智能体让系统从“展示”走向“建议”。
- **最后回到用户体验**：登录、导航、植物档案、聊天和设置页把能力收束成一个可用产品。

## Repository Notes

- `docs/接口文档.md`：接口说明与业务文档。
- `docs/用户故事.md`：产品场景与用户故事。
- `docs/mqtt接口文档`：硬件消息接入相关说明。
- `device/`：硬件模块资料与设备侧内容。

## License

本项目当前未声明开源许可证。若用于公开分发或课程展示，建议在后续补充 License、部署说明和环境变量模板。
