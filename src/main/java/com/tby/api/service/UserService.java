package com.tby.api.service;

import com.tby.api.db.User;
import com.tby.api.dto.CreateUserRequest;
import com.tby.api.exception.BusinessException;
import com.tby.api.exception.ErrorCode;
import com.tby.api.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final OrderService orderService;

    public User createUser(CreateUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        userMapper.insert(user);
        return user;
    }

    public User getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        return user;
    }

    public List<User> getAllUsers() {
        return userMapper.selectAll();
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        if (userMapper.selectById(userId) == null) throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        orderService.deleteOrdersByUserId(userId);
        userMapper.deleteById(userId);
    }
}
