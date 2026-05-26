package com.tby.api.mapper;

import com.tby.api.db.Order;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {

    @Select("SELECT * FROM orders WHERE order_id = #{orderId}")
    Order selectById(Long orderId);

    @Select("SELECT * FROM orders")
    List<Order> selectAll();

    @Select("SELECT * FROM orders WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Order> selectByUserId(Long userId);

    @Select("SELECT * FROM orders WHERE idempotency_key = #{idempotencyKey}")
    Order selectByIdempotencyKey(String idempotencyKey);

    @Insert("INSERT INTO orders(user_id, product_id, order_amount, total_price, created_at, idempotency_key) " +
            "VALUES(#{userId}, #{productId}, #{orderAmount}, #{totalPrice}, #{createdAt}, #{idempotencyKey})")
    @Options(useGeneratedKeys = true, keyProperty = "orderId")
    int insert(Order order);

    @Update("UPDATE orders SET order_amount = #{orderAmount}, total_price = #{totalPrice} " +
            "WHERE order_id = #{orderId}")
    int update(Order order);

    @Delete("DELETE FROM orders WHERE order_id = #{orderId}")
    int deleteById(Long orderId);

    @Delete("DELETE FROM orders WHERE user_id = #{userId}")
    int deleteByUserId(Long userId);
}
