package com.dp.chat.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dp.chat.service.DistributeService;
import com.dp.common.Name;

import java.sql.Timestamp;
import java.util.Date;

public class Message {
    private Long messageId;
    private Long rawId;
    private Long senderId;
    private Long receiverId;
    private Long groupId;
    private String pairName;
    private String content;
    private Timestamp time;
    private DistributeService ds;

    public Message(){}
    public Message(Long rawId, Long senderId, Long receiverId, String content) {
        this.rawId = rawId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
    }
    public Message(String rawMessage){
        JSONObject json = JSON.parseObject(rawMessage);
        rawId = Long.parseLong(json.get("rawId").toString());
        content = json.get("content").toString();
        if(json.containsKey("receiverId")){
            senderId = Long.parseLong(json.get("senderId").toString());
            receiverId = Long.parseLong(json.get("receiverId").toString());
            pairName = Name.pairName(senderId, receiverId);
        }
        else{
            senderId = Long.parseLong(json.get("senderId").toString());
            pairName = json.get("groupId").toString();
            groupId = Long.parseLong(pairName);
        }
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", rawId=" + rawId +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", content='" + content + '\'' +
                ", time=" + time +
                '}';
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public Long getSenderId() {
        return senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public Long getRawId() {
        return rawId;
    }

    public String getContent() {
        return content;
    }

    public String getPairName() {
        return pairName;
    }

    public Long getMessageId() {
        return messageId;
    }

    public Timestamp getTime() {
        return time;
    }

    public Long getGroupId(){
        return groupId;
    }

    public void setGroupId(Long groupId){
        this.groupId = groupId;
    }

    public DistributeService getDs() {
        return ds;
    }

    public void setDs(DistributeService ds) {
        this.ds = ds;
    }
}
