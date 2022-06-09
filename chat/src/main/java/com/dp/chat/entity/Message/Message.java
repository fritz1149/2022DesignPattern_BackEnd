package com.dp.chat.entity.Message;

import com.dp.chat.service.DistributeService;

public interface Message {
    public void distribute(DistributeService ds);
    public String getPairName();
    public String getMessageType();
}
