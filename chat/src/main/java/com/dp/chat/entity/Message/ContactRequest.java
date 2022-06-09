package com.dp.chat.entity.Message;

import com.dp.chat.entity.UserInfo;
import com.dp.chat.service.DistributeService;
import com.dp.common.Name;

import java.sql.Timestamp;

public class ContactRequest implements Message{
    @Override
    public void distribute(DistributeService ds) {
        ds.distributeContactRequest(this);
    }

    @Override
    public String getPairName() {
        return Name.pairName(senderId, receiverId);
    }

    @Override
    public String getMessageType() {
        return "contactRequest";
    }

    private Long senderId;
    private Long receiverId;
    private String type;
    private Timestamp time;
    private UserInfo userInfo;

    public ContactRequest() {
    }

    public ContactRequest(Long senderId, Long receiverId, String type, UserInfo userInfo) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.type = type;
        this.userInfo = userInfo;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
