package com.dp.chat.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dp.chat.dao.CacheDao;
import com.dp.chat.dao.GroupMapper;
import com.dp.chat.dao.StorageDao;
import com.dp.chat.entity.ChatTemplate;
import com.dp.common.Name;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
@Service
public class StaticService {
    @Autowired
    StorageDao storageDao;
    @Autowired
    GroupMapper groupMapper;
    @Autowired
    CacheDao cacheDao;
    public String claimStorage(Long userId1, Long userId2){
        Assert.notNull(userId1, "userId null");
        Assert.notNull(userId2, "userId null");
        String pairName = Name.pairName(userId1, userId2);
        return storageDao.claimStorage(pairName);
    }

    public JSONObject getChatLog(Long userId1, Long userId2, Integer pageSize, Integer pageNum){
        String pairName = Name.pairName(userId1, userId2);
        Integer offset = (pageNum - 1) * pageSize;
        Integer limit = pageSize;
        return storageDao.getLog(pairName, offset, limit);
    }

    public String claimGroup(String groupName, Long  userId){
        storageDao.claimGroup(groupName, userId);
        //群组成员存储
        return "OK";
    }

    public String joinGroup(Long userId, Long groupId){
        Assert.notNull(userId, "userId null");
        Assert.notNull(groupId, "groupId null");
        groupMapper.addMember(userId, groupId);
        cacheDao.addOnlineToGroup(userId, groupId);
        return "OK";
    }

    public String leaveGroup(Long userId, Long groupId){
        Assert.notNull(userId, "userId null");
        Assert.notNull(groupId, "groupId null");
        Long linesAffected = groupMapper.deleteMember(userId, groupId);
        if(linesAffected == 1L)
            return "OK";
        else if(linesAffected == 0L)
            return "user not in the group";
        else
            return "error";
    }
}
