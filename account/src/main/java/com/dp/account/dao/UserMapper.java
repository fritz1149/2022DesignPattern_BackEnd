package com.dp.account.dao;

import com.dp.account.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserMapper {
    public User getUserByName(@Param("name") String userName);
    public User getUserById(@Param("id") Long userId);
    public Long addUser(@Param("user") User user);
    public List<Long> getGroups(@Param("userId") Long userId);
}
