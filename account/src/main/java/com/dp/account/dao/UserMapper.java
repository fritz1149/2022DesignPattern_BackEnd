package com.dp.account.dao;

import com.dp.account.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserMapper {
    public List<User> getUserByName(@Param("name") String name);
    public List<User> getNonContactByName(@Param("name") String name, @Param("userId") Long userId);
    public User getUserById(@Param("id") Long userId);
    public Long addUser(@Param("user") User user);
    public List<Long> getGroups(@Param("userId") Long userId);
    public Long uploadAvatar(@Param("userId") Long userId, @Param("avatar") String avatar);
    public Long uploadSign(@Param("userId") Long userId, @Param("sign") String value);
    public Long uploadPassword(@Param("userId") Long userId, @Param("password") String password, @Param("salt") String salt);
}
