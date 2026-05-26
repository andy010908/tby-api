package com.tby.api.db;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {
    private Long orderId;
    private Long userId;
    private Long productId;
    private Integer orderAmount;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private String idempotencyKey;
}
