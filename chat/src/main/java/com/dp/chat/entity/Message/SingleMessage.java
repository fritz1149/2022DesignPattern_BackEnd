package com.dp.chat.entity.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dp.chat.service.DistributeService;
import com.dp.common.Name;

import java.sql.Timestamp;

public class SingleMessage implements Message{
    @Override
    public void distribute(DistributeService ds) {
        ds.distributeSingle(this);
    }

    @Override
    public String getMessageType() {
        return "singleMessage";
    }
    private Long messageId;
    private Long rawId;
    private Long senderId;
    private Long receiverId;
    private String pairName;
    private String content;
    private String contentType;
    private Timestamp time;

    public SingleMessage(){}
    public SingleMessage(Long rawId, Long senderId, Long receiverId, String content) {
        this.rawId = rawId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.pairName = Name.pairName(senderId, receiverId);
    }

    @Override
    public String toString() {
        return "SingleMessage{" +
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

    public void setRawId(Long rawId) {
        this.rawId = rawId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public void setPairName(String pairName) {
        this.pairName = pairName;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
