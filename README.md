# demo-miniapp-service

## 服务身份

- 服务名：demo-miniapp-service
- 主题域：小程序 E2E 基准
- 一句话职责：为 `demo-miniapp` 提供本地确定性 E2E baseline 后端能力。
- 默认端口：`18082`

## 当前 API

- `GET /api/e2e/health`：返回服务健康状态、服务名、版本和当前时间。

## 快速验证

```bash
mvn test
mvn spring-boot:run
curl http://127.0.0.1:18082/api/e2e/health
```

本服务只用于 demo 小程序 E2E baseline，不承载真实业务流程。
