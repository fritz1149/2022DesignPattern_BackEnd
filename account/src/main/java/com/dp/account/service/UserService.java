package com.dp.account.service;

import cn.dev33.satoken.secure.SaSecureUtil;
import com.dp.account.dao.UserMapper;
import com.dp.account.entity.User;
import com.dp.common.Name;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import  java.security.SecureRandom;

@Service
public class UserService{
    @Autowired
    UserMapper userMapper;
    @Autowired
    RedisTemplate redisTemplate;
    public enum LoginState{
        LOGIN_OK, USER_NOT_FOUND, PWD_ERROR
    }
    public LoginState loginCheck(Long userId, String password){
        User user = userMapper.getUserById(userId);
        if(user == null)
            return LoginState.USER_NOT_FOUND;
        password = SaSecureUtil.md5BySalt(password, user.getSalt());
        if(!password.equals(user.getUserPwdEncoded()))
            return LoginState.PWD_ERROR;
        return LoginState.LOGIN_OK;
    }

    public Long register(String userName, String password){
        String salt = generateSalt();
        password = SaSecureUtil.md5BySalt(password, salt);
        User user = new User(userName, password, salt);
        userMapper.addUser(user);

        Long userId = user.getUserId();
        redisTemplate.opsForValue().set(Name.lastId(userId), "-1");
        redisTemplate.opsForValue().set(Name.stateName(userId), "consistent");

        return userId;
    }

    private String generateSalt(){
        byte[] saltBytes = new byte[128];
        SecureRandom random = new SecureRandom();
        random.nextBytes(saltBytes);
        return new String(saltBytes);
    }
}
