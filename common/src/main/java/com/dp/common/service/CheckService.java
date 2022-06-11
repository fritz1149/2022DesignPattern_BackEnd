package com.dp.common.service;

import com.dp.common.dao.ExistMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class CheckService {
    @Autowired
    ExistMapper existMapper;

    public Boolean isValidUser(Long userId){
        Assert.notNull(userId, "userId null");
//        System.out.println("isValidUser: " + (existMapper.userExists(userId) == 1L));
        return existMapper.userExists(userId) == 1L;
    }

    public Boolean isValidGroup(Long groupId){
        Assert.notNull(groupId, "groupId null");
        return existMapper.groupExists(groupId) == 1L;
    }

    public Boolean isValidMember(Long userId, Long groupId){
        Assert.notNull(userId, "userId null");
        Assert.notNull(groupId, "groupId null");
        return existMapper.memberExists(userId, groupId) == 1L;
    }

    public Boolean isValidContact(Long userId, Long contactId){
        Assert.notNull(userId, "userId null");
        Assert.notNull(contactId, "contactId null");
        return existMapper.contactExists(userId, contactId) == 1L;
    }

    public Boolean isValidFile(String path){
        Assert.notNull(path, "path null");
        return existMapper.fileExists(path) == 1L;
    }
}
