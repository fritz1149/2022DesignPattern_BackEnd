package com.dp.account.service;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.dp.account.dao.CacheDao;
import com.dp.account.dao.UserMapper;
import com.dp.account.entity.User;
import com.dp.common.Name;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import  java.security.SecureRandom;
import java.util.List;

@Service
public class UserService{
    @Autowired
    UserMapper userMapper;
    @Autowired
    CacheDao cacheDao;
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

        List<Long> groupIds = userMapper.getGroups(userId);
        for(Long groupId : groupIds)
            System.out.println("groupId: " + groupId);
        cacheDao.claimOnlineToGroups(userId, userMapper.getGroups(userId));

        return LoginState.LOGIN_OK;
    }

    public void logout(Long userId){
        cacheDao.claimOfflineToGroups(userId, userMapper.getGroups(userId));
    }

    public Long register(String userName, String password){
        String salt = generateSalt();
        password = SaSecureUtil.md5BySalt(password, salt);
        User user = new User(userName, password, salt);
        userMapper.addUser(user);

        Long userId = user.getUserId();
        cacheDao.createUserContext(userId);

        return userId;
    }

    private String generateSalt(){
        byte[] saltBytes = new byte[128];
        SecureRandom random = new SecureRandom();
        random.nextBytes(saltBytes);
        return new String(saltBytes);
    }
}
