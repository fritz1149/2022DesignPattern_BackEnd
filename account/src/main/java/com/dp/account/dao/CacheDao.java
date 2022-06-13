package com.dp.account.dao;

import com.dp.chat.entity.ChatTemplate;
import com.dp.common.Name;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CacheDao {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    private MongoTemplate mongoTemplate;
    public void createUserContext(Long userId){
        redisTemplate.opsForValue().set(Name.lastId(userId), "0");
        redisTemplate.opsForValue().set(Name.stateName(userId), "consistent");

        Query query = new Query();
        String storageName = Name.storageName(userId);
        query.addCriteria(Criteria.where("pair_name").is(storageName));
        List<String> ret = mongoTemplate.find(query, String.class, "messages");
        if(ret.size() == 0) {
            mongoTemplate.insert(new ChatTemplate(0, storageName), "messages");
        }
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
