package com.tby.api.db;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCategory {
    private Long categoryId;
    private String categoryName;
    private BigDecimal taxRate;
}
