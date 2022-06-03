package com.dp.account.dao;

import com.dp.common.Name;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CacheDao {
    @Autowired
    RedisTemplate redisTemplate;
    public void createUserContext(Long userId){
        redisTemplate.opsForValue().set(Name.lastId(userId), "-1");
        redisTemplate.opsForValue().set(Name.stateName(userId), "consistent");
    }

    public void claimOnlineToGroups(Long userId, List<Long> groupIds){
        for(Long groupId : groupIds){
            redisTemplate.boundSetOps(Name.groupOnlineList(groupId)).add(userId.toString());
        }
    }

    public void claimOfflineToGroups(Long userId, List<Long> groupIds){
        for(Long groupId : groupIds){
            redisTemplate.boundSetOps(Name.groupOnlineList(groupId)).remove(userId.toString());
        }
    }
}
