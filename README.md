# tby-api

## 技術規範
- Java 17
- Spring Boot 3.5.x
- Maven
- MyBatis（Annotation，無 XML）
- MySQL 5.7
- Docker Compose

## 資料夾結構
```
controller/
service/
db/         ← entity
mapper/
dto/
exception/
```

## 規定
- 統一例外處理
- Order 注意 Race Condition，使用 `@Transactional`
- Order 需注意 Idempotency（Header: `Idempotency-Key`）
- 分頁使用 PageHelper，預設 20 筆

## API
- GET    /api/product/{productId}
- POST   /api/order
- PATCH  /api/order/{orderId}
- DELETE /api/order/{orderId}
- DELETE /api/user/{userId}
- GET    /api/order/{userId}
