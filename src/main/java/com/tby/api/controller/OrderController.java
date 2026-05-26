package com.tby.api.controller;

import com.tby.api.db.Order;
import com.tby.api.dto.ApiResponse;
import com.tby.api.dto.CreateOrderRequest;
import com.tby.api.dto.PageResponse;
import com.tby.api.dto.PatchOrderRequest;
import com.tby.api.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Order> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        return ApiResponse.created(orderService.createOrder(request, idempotencyKey));
    }

    @GetMapping
    public ApiResponse<List<Order>> getAllOrders() {
        return ApiResponse.ok(orderService.getAllOrders());
    }

    @GetMapping("/{userId}")
    public ApiResponse<PageResponse<Order>> getOrdersByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(orderService.getOrdersByUserId(userId, page, size));
    }

    @PatchMapping("/{orderId}")
    public ApiResponse<Order> patchOrder(@PathVariable Long orderId,
                                         @Valid @RequestBody PatchOrderRequest request) {
        return ApiResponse.ok(orderService.patchOrder(orderId, request));
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
    }
}
