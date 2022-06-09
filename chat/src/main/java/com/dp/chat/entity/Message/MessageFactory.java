package com.dp.chat.entity.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dp.chat.dao.ContactMapper;
import com.dp.common.Name;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.ws.Action;

@Repository
public class MessageFactory {
    @Autowired
    ContactMapper contactMapper;
    public Message parseRaw(String rawMessage){
        JSONObject json = JSON.parseObject(rawMessage);
        Long rawId = Long.parseLong(json.get("rawId").toString());
        String content = json.get("content").toString();
        if(json.containsKey("receiverId")){
            Long senderId = Long.parseLong(json.get("senderId").toString());
            Long receiverId = Long.parseLong(json.get("receiverId").toString());
            return new SingleMessage(rawId, senderId, receiverId, content);
        }
        else{
            Long senderId = Long.parseLong(json.get("senderId").toString());
            Long groupId = Long.parseLong(json.get("groupId").toString());
            return new GroupMessage(rawId, senderId, groupId, content);
        }
    }

    public Message contactRequest(Long senderId, Long receiverId, String type){
        return new ContactRequest(senderId, receiverId, type, contactMapper.getUserInfo(senderId));
    }
}
