package com.tby.api.db;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Product {
    private Long productId;
    private Long productCategoryId;
    private String productName;
    private BigDecimal unitPrice;
    private Integer stock;
}
