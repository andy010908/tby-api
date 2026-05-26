package com.tby.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateProductRequest {
    @NotNull(message = "productCategoryId is required")
    private Long productCategoryId;

    @NotBlank(message = "productName is required")
    private String productName;

    @NotNull(message = "unitPrice is required")
    @Positive(message = "unitPrice must be positive")
    private BigDecimal unitPrice;

    @Min(value = 0, message = "stock must be >= 0")
    private int stock;
}
