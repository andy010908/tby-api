package com.tby.api.controller;

import com.tby.api.db.ProductCategory;
import com.tby.api.dto.ApiResponse;
import com.tby.api.dto.CreateProductCategoryRequest;
import com.tby.api.service.ProductCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductCategory> createCategory(@Valid @RequestBody CreateProductCategoryRequest request) {
        return ApiResponse.created(productCategoryService.createCategory(request));
    }

    @GetMapping("/{categoryId}")
    public ApiResponse<ProductCategory> getCategory(@PathVariable Long categoryId) {
        return ApiResponse.ok(productCategoryService.getCategoryById(categoryId));
    }

    @GetMapping
    public ApiResponse<List<ProductCategory>> getAllCategories() {
        return ApiResponse.ok(productCategoryService.getAllCategories());
    }
}
