package com.tby.api.service;

import com.tby.api.db.Order;
import com.tby.api.db.Product;
import com.tby.api.db.ProductCategory;
import com.tby.api.db.User;
import com.tby.api.dto.CreateOrderRequest;
import com.tby.api.dto.PageResponse;
import com.tby.api.dto.PatchOrderRequest;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tby.api.exception.BusinessException;
import com.tby.api.exception.ErrorCode;
import com.tby.api.mapper.OrderMapper;
import com.tby.api.mapper.ProductCategoryMapper;
import com.tby.api.mapper.ProductMapper;
import com.tby.api.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.dao.DuplicateKeyException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final ProductCategoryMapper productCategoryMapper;
    private final UserMapper userMapper;

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public Order createOrder(CreateOrderRequest request, String idempotencyKey) {
        // 有帶 key 就先查：同一個 key 代表重試，直接回傳原本的結果
        if (idempotencyKey != null) {
            Order existing = orderMapper.selectByIdempotencyKey(idempotencyKey);
            if (existing != null) return existing;
        }

        User user = userMapper.selectById(request.getUserId());
        if (user == null) throw new BusinessException(ErrorCode.USER_NOT_FOUND);

        // SELECT FOR UPDATE — 鎖住 product row，並發請求依序排隊，防止 overselling
        Product product = productMapper.selectByIdForUpdate(request.getProductId());
        if (product == null) throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);

        if (product.getStock() < request.getOrderAmount()) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK,
                    "Available stock: " + product.getStock() + ", requested: " + request.getOrderAmount());
        }

        int updated = productMapper.decrementStock(product.getProductId(), request.getOrderAmount());
        if (updated == 0) throw new BusinessException(ErrorCode.STOCK_UPDATE_FAILED);

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setProductId(request.getProductId());
        order.setOrderAmount(request.getOrderAmount());
        order.setTotalPrice(calcTotal(product, request.getOrderAmount()));
        order.setCreatedAt(LocalDateTime.now());
        order.setIdempotencyKey(idempotencyKey);

        try {
            orderMapper.insert(order);
        } catch (DuplicateKeyException e) {
            // 極少數 race condition：兩個完全相同的 key 同時通過上面的 null 檢查
            // DB unique constraint 擋下第二個，回傳第一個已建立的 order
            return orderMapper.selectByIdempotencyKey(idempotencyKey);
        }
        return order;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public Order patchOrder(Long orderId, PatchOrderRequest request) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);

        int oldAmount = order.getOrderAmount();
        int newAmount = request.getOrderAmount();
        int diff = newAmount - oldAmount; // 正數=要多扣，負數=要還回去

        // 鎖住 product row 再調整庫存
        Product product = productMapper.selectByIdForUpdate(order.getProductId());
        if (product == null) throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);

        if (diff > 0) {
            if (product.getStock() < diff) {
                throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK,
                        "Available stock: " + product.getStock() + ", need extra: " + diff);
            }
            int updated = productMapper.decrementStock(product.getProductId(), diff);
            if (updated == 0) throw new BusinessException(ErrorCode.STOCK_UPDATE_FAILED);
        } else if (diff < 0) {
            productMapper.restoreStock(product.getProductId(), -diff);
        }

        order.setOrderAmount(newAmount);
        order.setTotalPrice(calcTotal(product, newAmount));
        orderMapper.update(order);
        return order;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);

        // 還庫存再刪訂單
        productMapper.restoreStock(order.getProductId(), order.getOrderAmount());
        orderMapper.deleteById(orderId);
    }

    public Order getOrderById(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        return order;
    }

    public List<Order> getAllOrders() {
        return orderMapper.selectAll();
    }

    public PageResponse<Order> getOrdersByUserId(Long userId, int page, int size) {
        if (userMapper.selectById(userId) == null) throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        PageHelper.startPage(page, size);
        List<Order> orders = orderMapper.selectByUserId(userId);
        PageInfo<Order> pageInfo = new PageInfo<>(orders);
        return PageResponse.of(pageInfo.getList(), page, size, pageInfo.getTotal());
    }

    // 刪 user 時 cascade 用，不還庫存
    void deleteOrdersByUserId(Long userId) {
        orderMapper.deleteByUserId(userId);
    }

    private BigDecimal calcTotal(Product product, int amount) {
        ProductCategory category = productCategoryMapper.selectById(product.getProductCategoryId());
        BigDecimal taxRate = (category != null) ? category.getTaxRate() : BigDecimal.ZERO;
        return product.getUnitPrice()
                .multiply(BigDecimal.valueOf(amount))
                .multiply(BigDecimal.ONE.add(taxRate))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
