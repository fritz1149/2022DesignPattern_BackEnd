package com.dp.chat.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dp.chat.entity.Message.Message;
import com.dp.common.Name;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class CacheDao {
    @Autowired
    private RedisTemplate redisTemplate;

    private JSONArray execLuaScript(ResourceScriptSource script, String ...keys){
        DefaultRedisScript<List> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(script);
        redisScript.setResultType(List.class);
        List<String> keyList = new ArrayList<>();
        for(String key : keys)
            keyList.add(key);
        JSONArray result = (JSONArray) JSONObject.toJSON(redisTemplate.execute(redisScript, keyList));

        return result;
    }

    public JSONArray appendToCache(Message message, Long userId){
        ResourceScriptSource appendScript = new ResourceScriptSource(new ClassPathResource("redis/append.lua"));
        return execLuaScript(appendScript, Name.listName(userId), JSONObject.toJSONString(message),
                Name.stateName(userId), Name.lastId(userId));
    }

    public JSONArray appendToGroupCache(Message message, Long groupId){
        ResourceScriptSource appendScript = new ResourceScriptSource(new ClassPathResource("redis/appendGroup.lua"));
        return execLuaScript(appendScript, Name.groupListName(groupId), JSONObject.toJSONString(message));
    }

    public JSONArray markOffline(Long userId){
        ResourceScriptSource markScript = new ResourceScriptSource(new ClassPathResource("redis/markOffline.lua"));
        return execLuaScript(markScript, Name.listName(userId), Name.stateName(userId));
    }

    public JSONArray popCache(String lastId, String listName, Long clientLatestId){
        return execLuaScript(new ResourceScriptSource(new ClassPathResource("redis/pop.lua")),
                lastId, listName, clientLatestId.toString());
    }

    public JSONArray checkPush(String listName, String stateName, String lastId){
        return execLuaScript(new ResourceScriptSource(new ClassPathResource("redis/push.lua")),
                listName, stateName, lastId);
    }

    public Set getGroupOnlineMembers(Long groupId){
        return redisTemplate.boundSetOps(Name.groupOnlineList(groupId)).members();
    }

    public void addOnlineToGroup(Long userId, Long groupId){
        redisTemplate.boundSetOps(Name.groupOnlineList(groupId)).add(userId.toString());
    }

    public JSONArray shouldPush(Long groupId, Long clientLatestId){
        return execLuaScript(new ResourceScriptSource(new ClassPathResource("redis/pushGroup.lua")),
                Name.groupListName(groupId), clientLatestId.toString());
    }
}
