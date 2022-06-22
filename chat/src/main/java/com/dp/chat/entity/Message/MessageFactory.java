package com.dp.chat.entity.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dp.chat.dao.ContactMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MessageFactory {
    @Autowired
    ContactMapper contactMapper;
    public Message parseRaw(String rawMessage){
        JSONObject json = JSON.parseObject(rawMessage);
        String content = (String)json.get("content");
        Object contentType = json.get("contentType");
        String type = (String)json.get("type");
        if(json.containsKey("type") && (type.equals("request") || type.equals("response"))){
            Long senderId = Long.parseLong(json.get("senderId").toString());
            Long receiverId = Long.parseLong(json.get("receiverId").toString());
            return new ContactRequest(senderId, receiverId, type, contactMapper.getUserInfo(senderId));
        }
        else if(json.containsKey("receiverId") && !json.containsKey("groupId")){
            Long senderId = Long.parseLong(json.get("senderId").toString());
            Long receiverId = Long.parseLong(json.get("receiverId").toString());
            Long rawId = Long.parseLong(json.get("rawId").toString());
            SingleMessage sg = new SingleMessage(rawId, senderId, receiverId, content);
            if(contentType != null)
                sg.setContentType(contentType.toString());
            return sg;
        }
        else{
            Long senderId = Long.parseLong(json.get("senderId").toString());
            Long groupId = Long.parseLong(json.get("groupId").toString());
            Long rawId = Long.parseLong(json.get("rawId").toString());
            GroupMessage gm = new GroupMessage(rawId, senderId, groupId, content);
            if(contentType != null)
                gm.setContentType(contentType.toString());
            return gm;
        }
    }

    public Message contactRequest(Long senderId, Long receiverId, String type){
        return new ContactRequest(senderId, receiverId, type, contactMapper.getUserInfo(senderId));
    }
}
