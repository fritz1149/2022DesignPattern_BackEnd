package com.dp.chat.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dp.chat.dao.CacheDao;
import com.dp.chat.dao.SocketDao;
import com.dp.chat.dao.StorageDao;
import com.dp.chat.entity.Ack;
import com.dp.chat.entity.Message;
import com.dp.common.Enum.State;
import com.dp.common.Name;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.ListIterator;

@Service
public class DistributeServiceSingleImpl implements DistributeService{
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
        Long senderId = message.getSenderId();
        Long receiverId = message.getReceiverId();
        JSONArray res = cacheDao.appendToCache(message, receiverId);
        if (res.get(0).toString().equals("true"))
            if (socketDao.sendMessages(receiverId,
                    Long.valueOf(res.get(1).toString()) - 1L, new JSONArray().fluentAdd(message))
                    == State.TARGET_NOT_FOUND)
                cacheDao.markOffline(receiverId);

        res = cacheDao.appendToCache(message, senderId);
        if (res.get(0).toString().equals("true"))
            if (socketDao.sendMessages(senderId,
                    Long.valueOf(res.get(1).toString()) - 1L, new JSONArray().fluentAdd(message))
                    == State.TARGET_NOT_FOUND)
                cacheDao.markOffline(senderId);
    }

    public void receiveAck(Ack ack){
        Long userId = ack.getUserId();
        Long clientLatestId = ack.getClientLatestId();
        String lastId = Name.lastId(userId);
        String listName = Name.listName(userId);
        String stateName = Name.stateName(userId);

        cacheDao.popCache(lastId, listName, clientLatestId);
        JSONArray res = cacheDao.checkPush(listName, stateName, lastId);

        if(res.get(0).toString().equals("true")) {
            JSONArray messages = res.getJSONArray(2);
            ListIterator<Object> iter = messages.listIterator();
            while (iter.hasNext()) {
                String json = iter.next().toString();
                iter.set(JSONObject.parse(json));
            }
            socketDao.sendMessages(userId, Long.valueOf(res.get(1).toString()), messages);
        }
        else
            socketDao.sendInfo(userId, clientLatestId, "consistent");
    }
}
