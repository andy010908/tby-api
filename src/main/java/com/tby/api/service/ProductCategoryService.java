package com.tby.api.service;

import com.tby.api.db.ProductCategory;
import com.tby.api.dto.CreateProductCategoryRequest;
import com.tby.api.exception.BusinessException;
import com.tby.api.exception.ErrorCode;
import com.tby.api.mapper.ProductCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryMapper productCategoryMapper;

    public ProductCategory createCategory(CreateProductCategoryRequest request) {
        ProductCategory category = new ProductCategory();
        category.setCategoryName(request.getCategoryName());
        category.setTaxRate(request.getTaxRate());
        productCategoryMapper.insert(category);
        return category;
    }

    public ProductCategory getCategoryById(Long categoryId) {
        ProductCategory category = productCategoryMapper.selectById(categoryId);
        if (category == null) throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        return category;
    }

    public List<ProductCategory> getAllCategories() {
        return productCategoryMapper.selectAll();
    }
}
