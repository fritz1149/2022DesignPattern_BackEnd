package com.dp.account.dao;

import com.dp.account.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {
    public User getUserByName(@Param("name") String userName);
    public User getUserById(@Param("id") Long userId);
    public Long addUser(@Param("user") User user);
}
