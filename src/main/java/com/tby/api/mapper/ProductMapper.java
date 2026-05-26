package com.tby.api.mapper;

import com.tby.api.db.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Select("SELECT * FROM product WHERE product_id = #{productId}")
    Product selectById(Long productId);

    // Pessimistic lock: prevents concurrent reads until transaction commits
    @Select("SELECT * FROM product WHERE product_id = #{productId} FOR UPDATE")
    Product selectByIdForUpdate(Long productId);

    @Select("SELECT * FROM product")
    List<Product> selectAll();

    @Insert("INSERT INTO product(product_category_id, product_name, unit_price, stock) " +
            "VALUES(#{productCategoryId}, #{productName}, #{unitPrice}, #{stock})")
    @Options(useGeneratedKeys = true, keyProperty = "productId")
    int insert(Product product);

    // Atomic decrement with guard to prevent negative stock
    @Update("UPDATE product SET stock = stock - #{amount} " +
            "WHERE product_id = #{productId} AND stock >= #{amount}")
    int decrementStock(@Param("productId") Long productId, @Param("amount") int amount);

    @Update("UPDATE product SET stock = stock + #{amount} WHERE product_id = #{productId}")
    int restoreStock(@Param("productId") Long productId, @Param("amount") int amount);
}
