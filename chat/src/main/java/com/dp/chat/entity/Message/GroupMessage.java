package com.dp.chat.entity.Message;

import com.dp.chat.service.DistributeService;

import java.sql.Timestamp;

public class GroupMessage implements Message{
    @Override
    public void distribute(DistributeService ds) {
        ds.distributeGroup(this);
    }

    @Override
    public String getPairName() {
        return groupId.toString();
    }

    @Override
    public String getMessageType() {
        return "groupMessage";
    }

    private Long messageId;
    private Long rawId;
    private Long senderId;
    private Long groupId;
    private String content;
    private Timestamp time;

    public GroupMessage() {
    }

    public GroupMessage(Long rawId, Long senderId, Long groupId, String content) {
        this.rawId = rawId;
        this.senderId = senderId;
        this.groupId = groupId;
        this.content = content;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getRawId() {
        return rawId;
    }

    public void setRawId(Long rawId) {
        this.rawId = rawId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
