package com.tby.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PatchOrderRequest {
    @NotNull(message = "orderAmount is required")
    @Min(value = 1, message = "orderAmount must be at least 1")
    private Integer orderAmount;
}
