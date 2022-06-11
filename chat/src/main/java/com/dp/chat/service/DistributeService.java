package com.dp.chat.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dp.chat.dao.CacheDao;
import com.dp.chat.dao.SocketDao;
import com.dp.chat.dao.StorageDao;
import com.dp.chat.entity.Ack;
import com.dp.chat.entity.Message.ContactRequest;
import com.dp.chat.entity.Message.GroupMessage;
import com.dp.chat.entity.Message.Message;
import com.dp.chat.entity.Message.SingleMessage;
import com.dp.common.Enum.State;
import com.dp.common.Name;
import com.dp.common.service.CheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.ListIterator;

@Service
public class DistributeService {
    @Autowired
    private CacheDao cacheDao;
    @Autowired
    private SocketDao socketDao;
    @Autowired
    private StorageDao storageDao;
    @Autowired
    private CheckService checkService;

    public void pushSingle(Message message, Long receiverId){
        if(!checkService.isValidUser(receiverId))
            return;
        JSONArray res = cacheDao.appendToCache(message, receiverId);
        if (res.get(0).toString().equals("true"))
            if (socketDao.sendMessages(receiverId,
                    Long.valueOf(res.get(1).toString()) - 1L, new JSONArray().fluentAdd(message))
                    == State.TARGET_NOT_FOUND)
                cacheDao.markOffline(receiverId);
    }

    public void distributeSingle(SingleMessage message){
        message.setTime(new Timestamp(new Date().getTime()));
        JSONObject ret = storageDao.saveToStorage(message);
        Long lastId = Long.valueOf(ret.get("number").toString());
        message.setMessageId(lastId);

        Long senderId = message.getSenderId();
        Long receiverId = message.getReceiverId();
        pushSingle(message, receiverId);
        pushSingle(message, senderId);
    }

    public void distributeContactRequest(ContactRequest message){
        message.setTime(new Timestamp(new Date().getTime()));
        Long receiverId = message.getReceiverId();
        pushSingle(message, receiverId);
    }

    public void distributeGroup(GroupMessage message){
        Long groupId = message.getGroupId();
        if(!checkService.isValidGroup(groupId))
            return;

        message.setTime(new Timestamp(new Date().getTime()));
        JSONObject ret = storageDao.saveToStorage(message);
        Long lastId = Long.valueOf(ret.get("number").toString());
        message.setMessageId(lastId);

        JSONArray res = cacheDao.appendToGroupCache(message, message.getGroupId());
        Long serverLatestId = res.getLong(0) - 2L;
        for(Object a : cacheDao.getGroupOnlineMembers(message.getGroupId())){
            Long userId = Long.parseLong(a.toString());
            socketDao.sendGroupMessages(userId, serverLatestId, groupId, new JSONArray().fluentAdd(message));
        }
    }

    public void receiveAckSingle(Ack ack){
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

    public void receiveAckGroup(Ack ack){
        Long userId = ack.getUserId();
        Long groupId = ack.getTargetId();
        Long clientLatestId = ack.getClientLatestId();
        JSONArray messages = cacheDao.shouldPush(groupId, clientLatestId);
        ListIterator<Object> iter = messages.listIterator();
        while (iter.hasNext()) {
            String json = iter.next().toString();
            iter.set(JSONObject.parse(json));
        }
        socketDao.sendGroupMessages(userId, clientLatestId, groupId, messages);
    }
}
