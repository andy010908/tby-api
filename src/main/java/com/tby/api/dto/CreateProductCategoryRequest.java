package com.tby.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateProductCategoryRequest {
    @NotBlank(message = "categoryName is required")
    private String categoryName;

    @NotNull(message = "taxRate is required")
    @DecimalMin(value = "0.0", message = "taxRate must be >= 0")
    private BigDecimal taxRate;
}
