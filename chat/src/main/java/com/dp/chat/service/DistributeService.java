package com.dp.chat.service;

import com.dp.chat.entity.Ack;
import com.dp.chat.entity.Message;

public interface DistributeService {
    public void saveToStorage(Message message);
    public void saveToPushList(Message message);
    public void receiveAck(Ack ack);
}
