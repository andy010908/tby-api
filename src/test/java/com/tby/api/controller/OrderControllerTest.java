package com.tby.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tby.api.db.Order;
import com.tby.api.dto.CreateOrderRequest;
import com.tby.api.dto.PatchOrderRequest;
import com.tby.api.exception.BusinessException;
import com.tby.api.exception.ErrorCode;
import com.tby.api.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    private Order sampleOrder;

    @BeforeEach
    void setUp() {
        sampleOrder = new Order();
        sampleOrder.setOrderId(1L);
        sampleOrder.setUserId(3L);
        sampleOrder.setProductId(1L);
        sampleOrder.setOrderAmount(2);
        sampleOrder.setTotalPrice(new BigDecimal("62.99"));
        sampleOrder.setCreatedAt(LocalDateTime.of(2026, 5, 9, 10, 0));
    }

    // ── POST /api/order ─────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/order — 成功建立訂單，回傳 201")
    void createOrder_success() throws Exception {
        CreateOrderRequest req = new CreateOrderRequest();
        req.setUserId(3L);
        req.setProductId(1L);
        req.setOrderAmount(2);

        when(orderService.createOrder(any(), any())).thenReturn(sampleOrder);

        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.data.orderId").value(1))
                .andExpect(jsonPath("$.data.orderAmount").value(2));
    }

    @Test
    @DisplayName("POST /api/order — user 不存在，回傳 404")
    void createOrder_userNotFound() throws Exception {
        CreateOrderRequest req = new CreateOrderRequest();
        req.setUserId(999L);
        req.setProductId(1L);
        req.setOrderAmount(2);

        when(orderService.createOrder(any(), any()))
                .thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    @DisplayName("POST /api/order — 庫存不足，回傳 409")
    void createOrder_insufficientStock() throws Exception {
        CreateOrderRequest req = new CreateOrderRequest();
        req.setUserId(3L);
        req.setProductId(1L);
        req.setOrderAmount(999);

        when(orderService.createOrder(any(), any()))
                .thenThrow(new BusinessException(ErrorCode.INSUFFICIENT_STOCK,
                        "Available stock: 5, requested: 999"));

        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("Available stock: 5, requested: 999"));
    }

    @Test
    @DisplayName("POST /api/order — 缺少必填欄位，回傳 400")
    void createOrder_validationFail() throws Exception {
        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @DisplayName("POST /api/order — orderAmount = 0，回傳 400")
    void createOrder_amountZero() throws Exception {
        CreateOrderRequest req = new CreateOrderRequest();
        req.setUserId(3L);
        req.setProductId(1L);
        req.setOrderAmount(0);

        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    // ── PATCH /api/order/{order_id} ─────────────────────────────────

    @Test
    @DisplayName("PATCH /api/order/{id} — 成功更新數量，回傳 200")
    void patchOrder_success() throws Exception {
        sampleOrder.setOrderAmount(3);
        sampleOrder.setTotalPrice(new BigDecimal("94.49"));

        PatchOrderRequest req = new PatchOrderRequest();
        req.setOrderAmount(3);

        when(orderService.patchOrder(eq(1L), any())).thenReturn(sampleOrder);

        mockMvc.perform(patch("/api/order/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.orderAmount").value(3));
    }

    @Test
    @DisplayName("PATCH /api/order/{id} — 訂單不存在，回傳 404")
    void patchOrder_notFound() throws Exception {
        PatchOrderRequest req = new PatchOrderRequest();
        req.setOrderAmount(3);

        when(orderService.patchOrder(eq(999L), any()))
                .thenThrow(new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        mockMvc.perform(patch("/api/order/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Order not found"));
    }

    @Test
    @DisplayName("PATCH /api/order/{id} — 庫存不足，回傳 409")
    void patchOrder_insufficientStock() throws Exception {
        PatchOrderRequest req = new PatchOrderRequest();
        req.setOrderAmount(999);

        when(orderService.patchOrder(eq(1L), any()))
                .thenThrow(new BusinessException(ErrorCode.INSUFFICIENT_STOCK,
                        "Available stock: 3, need extra: 997"));

        mockMvc.perform(patch("/api/order/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409));
    }

    // ── DELETE /api/order/{order_id} ────────────────────────────────

    @Test
    @DisplayName("DELETE /api/order/{id} — 成功刪除，回傳 204")
    void deleteOrder_success() throws Exception {
        doNothing().when(orderService).deleteOrder(1L);

        mockMvc.perform(delete("/api/order/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/order/{id} — 訂單不存在，回傳 404")
    void deleteOrder_notFound() throws Exception {
        doThrow(new BusinessException(ErrorCode.ORDER_NOT_FOUND))
                .when(orderService).deleteOrder(999L);

        mockMvc.perform(delete("/api/order/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Order not found"));
    }

    // ── GET /api/order/{userId} ──────────────────────────────────────

    @Test
    @DisplayName("GET /api/order/{userId} — 成功回傳該 user 的訂單列表")
    void getOrdersByUser_success() throws Exception {
        when(orderService.getOrdersByUserId(eq(3L), anyInt(), anyInt())).thenReturn(new com.tby.api.dto.PageResponse<>(List.of(sampleOrder), 1, 20, 1, 1));

        mockMvc.perform(get("/api/order/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].userId").value(3));
    }

    @Test
    @DisplayName("GET /api/order/{userId} — user 不存在，回傳 404")
    void getOrdersByUser_userNotFound() throws Exception {
        when(orderService.getOrdersByUserId(eq(999L), anyInt(), anyInt()))
                .thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

        mockMvc.perform(get("/api/order/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    @DisplayName("GET /api/order/{userId} — user 無訂單，回傳空列表")
    void getOrdersByUser_emptyList() throws Exception {
        when(orderService.getOrdersByUserId(eq(3L), anyInt(), anyInt())).thenReturn(new com.tby.api.dto.PageResponse<>(List.of(), 1, 20, 0, 0));

        mockMvc.perform(get("/api/order/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(0));
    }
}
