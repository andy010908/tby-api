package com.tby.api.service;

import com.tby.api.db.Product;
import com.tby.api.dto.CreateProductRequest;
import com.tby.api.exception.BusinessException;
import com.tby.api.exception.ErrorCode;
import com.tby.api.mapper.ProductCategoryMapper;
import com.tby.api.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;
    private final ProductCategoryMapper productCategoryMapper;

    public Product createProduct(CreateProductRequest request) {
        if (productCategoryMapper.selectById(request.getProductCategoryId()) == null) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        Product product = new Product();
        product.setProductCategoryId(request.getProductCategoryId());
        product.setProductName(request.getProductName());
        product.setUnitPrice(request.getUnitPrice());
        product.setStock(request.getStock());
        productMapper.insert(product);
        return product;
    }

    public Product getProductById(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        return product;
    }

    public List<Product> getAllProducts() {
        return productMapper.selectAll();
    }
}
