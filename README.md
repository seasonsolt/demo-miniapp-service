# demo-miniapp-service

## 服务身份

- 服务名：demo-miniapp-service
- 主题域：小程序 E2E 基准
- 一句话职责：为 `demo-miniapp` 提供本地确定性 E2E baseline 后端能力。
- 默认端口：`18082`

## 当前 API

- `GET /api/e2e/health`：返回服务健康状态、服务名、版本和当前时间。
- `POST /api/e2e/login`：使用固定测试账号换取短期随机 token。
- `GET /api/e2e/session`：使用 `Authorization: Bearer <token>` 查询当前测试会话。
- `GET /api/e2e/protected-probe`：验证受保护请求的 token 拒绝和通过行为。
- `PUT /api/e2e/probes/{probeId}`：写入 `TEST_` 探针数据。
- `GET /api/e2e/probes/{probeId}`：读取 `TEST_` 探针数据。
- `DELETE /api/e2e/probes/{probeId}`：重置 `TEST_` 探针数据。
- `GET /api/e2e/error-probe/{errorType}`：按 `bad-request` / `server-error` 稳定返回 400 / 500 错误。

测试账号：

- account：`test-user`
- password：`test-password`

会话 token 仅保存在服务内存中，不使用 JWT 或外部存储；默认有效期为 900 秒。探针数据保存在 H2 in-memory 中，且只允许 `TEST_` 前缀 ID。

## 快速验证

```bash
mvn test
mvn spring-boot:run
curl http://127.0.0.1:18082/api/e2e/health
curl -X POST http://127.0.0.1:18082/api/e2e/login \
  -H 'Content-Type: application/json' \
  -d '{"account":"test-user","password":"test-password"}'
```

本服务只用于 demo 小程序 E2E baseline，不承载真实业务流程。
