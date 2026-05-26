package com.tby.api.controller;

import com.tby.api.db.Product;
import com.tby.api.dto.ApiResponse;
import com.tby.api.dto.CreateProductRequest;
import com.tby.api.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Product> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ApiResponse.created(productService.createProduct(request));
    }

    @GetMapping("/{productId}")
    public ApiResponse<Product> getProduct(@PathVariable Long productId) {
        return ApiResponse.ok(productService.getProductById(productId));
    }

    @GetMapping
    public ApiResponse<List<Product>> getAllProducts() {
        return ApiResponse.ok(productService.getAllProducts());
    }
}
