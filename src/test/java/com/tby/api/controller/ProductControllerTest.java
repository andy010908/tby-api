package com.tby.api.controller;

import com.tby.api.db.Product;
import com.tby.api.exception.BusinessException;
import com.tby.api.exception.ErrorCode;
import com.tby.api.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("GET /api/product/{id} — 成功回傳商品")
    void getProductById_success() throws Exception {
        Product product = new Product();
        product.setProductId(1L);
        product.setProductCategoryId(1L);
        product.setProductName("Wireless Mouse");
        product.setUnitPrice(new BigDecimal("29.99"));
        product.setStock(100);

        when(productService.getProductById(1L)).thenReturn(product);

        mockMvc.perform(get("/api/product/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.productId").value(1))
                .andExpect(jsonPath("$.data.productName").value("Wireless Mouse"))
                .andExpect(jsonPath("$.data.stock").value(100));
    }

    @Test
    @DisplayName("GET /api/product/{id} — 商品不存在，回傳 404")
    void getProductById_notFound() throws Exception {
        when(productService.getProductById(999L))
                .thenThrow(new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        mockMvc.perform(get("/api/product/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Product not found"));
    }
}
