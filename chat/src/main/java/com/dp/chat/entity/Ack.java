package com.dp.chat.entity;

import com.dp.chat.service.DistributeService;

public class Ack {
    private Long userId;
    private Long clientLatestId;
    private Long targetId;
    private DistributeService ds;

    public Ack() {
    }

    public Ack(Long userId, Long clientLatestId) {
        this.userId = userId;
        this.clientLatestId = clientLatestId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getClientLatestId() {
        return clientLatestId;
    }

    public void setClientLatestId(Long clientLatestId) {
        this.clientLatestId = clientLatestId;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public DistributeService getDs() {
        return ds;
    }

    public void setDs(DistributeService ds) {
        this.ds = ds;
    }
}
