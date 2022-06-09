package com.dp.account.service.searchService;

import com.dp.account.dao.UserMapper;
import com.dp.account.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class AllSearch implements SearchService {
    @Autowired
    UserMapper userMapper;
    @Override
    public List<User> getUserByName(String name, Long userId) {
        Assert.notNull(name, "name null");
        Assert.notNull(userId, "userId null");
        return userMapper.getUserByName(name);
    }
}
