package com.dp.chat.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dp.chat.dao.CacheDao;
import com.dp.chat.dao.SocketDao;
import com.dp.chat.dao.StorageDao;
import com.dp.chat.entity.Ack;
import com.dp.chat.entity.Message;
import com.dp.common.Enum;
import com.dp.common.Name;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.ListIterator;

@Service
public class DistributeServiceGroupImpl implements DistributeService{
    @Autowired
    private CacheDao cacheDao;
    @Autowired
    private SocketDao socketDao;
    @Autowired
    private StorageDao storageDao;

    public void saveToStorage(Message message){
        message.setTime(new Timestamp(new Date().getTime()));
        JSONObject ret = storageDao.saveToStorage(message);
        Long lastId = Long.valueOf(ret.get("number").toString());
        message.setMessageId(lastId);
    }

    public void saveToPushList(Message message){
        JSONArray res = cacheDao.appendToGroupCache(message, message.getGroupId());
        Long serverLatestId = res.getLong(0) - 1L;
        for(Object a : cacheDao.getGroupOnlineMembers(message.getGroupId())){
            Long userId = Long.parseLong(a.toString());
            socketDao.sendMessages(userId, serverLatestId, new JSONArray().fluentAdd(message));
        }
    }

    public void receiveAck(Ack ack){
        Long userId = ack.getUserId();
        Long targetId = ack.getTargetId();
        Long clientLatestId = ack.getClientLatestId();
        JSONArray messages = cacheDao.shouldPush(targetId, clientLatestId);
        ListIterator<Object> iter = messages.listIterator();
        while (iter.hasNext()) {
            String json = iter.next().toString();
            iter.set(JSONObject.parse(json));
        }
        socketDao.sendMessages(userId, clientLatestId, messages);
    }
}