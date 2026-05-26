package com.tby.api.mapper;

import com.tby.api.db.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM users WHERE user_id = #{userId}")
    User selectById(Long userId);

    @Select("SELECT * FROM users")
    List<User> selectAll();

    @Insert("INSERT INTO users(username) VALUES(#{username})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int insert(User user);

    @Delete("DELETE FROM users WHERE user_id = #{userId}")
    int deleteById(Long userId);
}
