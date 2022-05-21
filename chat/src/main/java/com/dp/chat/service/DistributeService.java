package com.dp.chat.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dp.chat.entity.Message;
import com.dp.common.Name;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

@Service
public class DistributeService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RemoteService remoteService;

    public void saveToStorage(Message message){
        message.setTime(new Timestamp(new Date().getTime()));

        Query query = new Query();
        query.addCriteria(Criteria.where("pair_name").is(message.getPairName()));
        query.fields().exclude("_id").include("number");

        Update update = new Update();
        update.push("messages", message);
        update.inc("number", 1);

        JSONObject ret = mongoTemplate.findAndModify(query, update, JSONObject.class, "messages");
        Assert.notNull(ret, "mongodb null !");
        Long lastId = Long.valueOf(ret.get("number").toString());
        Assert.notNull(lastId, "lastId null !");
        message.setMessageId(lastId);
    }

    private JSONArray execLuaScript(ResourceScriptSource script, String ...keys){
        DefaultRedisScript<List> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(script);
        redisScript.setResultType(List.class);
        List<String> keyList = new ArrayList<>();
        for(String key : keys)
            keyList.add(key);
        JSONArray result = (JSONArray) JSONObject.toJSON(redisTemplate.execute(redisScript, keyList));

        System.out.println(result.toJSONString());

        return result;
    }

    private void sendMessages(Long userId, Long serverLatestId, JSONArray messages){
        JSONObject data = new JSONObject();
        data.put("messages", messages);
        data.put("type", "message");
        data.put("start", serverLatestId + 1);
        System.out.println("prepare to send: " + data.toJSONString());
        remoteService.send(userId, data.toJSONString());
    }

    public void saveToPushList(Message message){
        Long senderId = message.getSenderId();
        Long receiverId = message.getReceiverId();

        ResourceScriptSource appendScript = new ResourceScriptSource(new ClassPathResource("redis/append.lua"));
        JSONArray res = execLuaScript(appendScript, Name.listName(receiverId), JSONObject.toJSONString(message),
                Name.stateName(receiverId), Name.lastId(receiverId));
        if(res.get(0).toString().equals("true"))
            sendMessages(receiverId, Long.valueOf(res.get(1).toString()), new JSONArray().fluentAdd(message));

        res = execLuaScript(appendScript, Name.listName(senderId), JSONObject.toJSONString(message),
                Name.stateName(senderId), Name.lastId(senderId));
        if(res.get(0).toString().equals("true"))
            sendMessages(senderId, Long.valueOf(res.get(1).toString()), new JSONArray().fluentAdd(message));
    }

    public void popPushList(Long userId, Long clientLatestId){
        String lastId = Name.lastId(userId);
        String listName = Name.listName(userId);
        String stateName = Name.stateName(userId);
         execLuaScript(new ResourceScriptSource(new ClassPathResource("redis/pop.lua")),
                lastId, listName, clientLatestId.toString());
        JSONArray res = execLuaScript(new ResourceScriptSource(new ClassPathResource("redis/push.lua")),
                listName, stateName, lastId);

        if(res.get(0).toString().equals("true")) {
            JSONArray messages = res.getJSONArray(2);
            ListIterator<Object> iter = messages.listIterator();
            while (iter.hasNext()) {
                String json = iter.next().toString();
                iter.set(JSONObject.parse(json));
            }
            sendMessages(userId, Long.valueOf(res.get(1).toString()), messages);
        }
    }
}
