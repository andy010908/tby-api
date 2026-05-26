package com.tby.api.mapper;

import com.tby.api.db.ProductCategory;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductCategoryMapper {

    @Select("SELECT * FROM product_category WHERE category_id = #{categoryId}")
    ProductCategory selectById(Long categoryId);

    @Select("SELECT * FROM product_category")
    List<ProductCategory> selectAll();

    @Insert("INSERT INTO product_category(category_name, tax_rate) VALUES(#{categoryName}, #{taxRate})")
    @Options(useGeneratedKeys = true, keyProperty = "categoryId")
    int insert(ProductCategory category);
}
