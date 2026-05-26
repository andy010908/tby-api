package com.tby.api.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND(404, "User not found"),
    PRODUCT_NOT_FOUND(404, "Product not found"),
    CATEGORY_NOT_FOUND(404, "Product category not found"),
    ORDER_NOT_FOUND(404, "Order not found"),
    INSUFFICIENT_STOCK(409, "Insufficient stock"),
    STOCK_UPDATE_FAILED(409, "Stock update failed due to concurrent modification"),
    INVALID_ARGUMENT(400, "Invalid argument");

    private final int httpStatus;
    private final String message;

    ErrorCode(int httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
