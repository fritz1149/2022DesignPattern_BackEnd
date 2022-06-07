package com.dp.chat.service;

import com.alibaba.fastjson.JSONObject;
import com.dp.chat.dao.ColonyDao;
import com.dp.chat.entity.Message;
import com.dp.common.Enum.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;

@Service
public class AppendService {
    private ConcurrentHashMap<String, AtomicInteger> pairs = new ConcurrentHashMap<>();
    private static final int MessagesNumberLimit = 10000;
    private static final int MessagesTimeLimit = 5;
    @Autowired
    private ColonyDao colonyDao;
    public ArrayBlockingQueue<Message> messages = new ArrayBlockingQueue<Message>(MessagesNumberLimit);
    private Boolean healthy = true;

    public AppendService(){
        Runnable workload = new Runnable(){
            @Override
            public void run() {
                while(true) {
                    try {
                        Message message = messages.take();
                        String pairName = message.getPairName();
                        if (!pairs.containsKey(pairName))
                            continue;
                        message.getDs().saveToStorage(message);
                        message.getDs().saveToPushList(message);
                        if (pairs.get(pairName).decrementAndGet() <= 0)
                            stopToHold(pairName);
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
//                healthy = false;
            }
        };
        Thread work = new Thread(workload);
        work.start();
    }

    public State append(String pairName, Message message){
        try {
            pairs.get(pairName).incrementAndGet();
            messages.offer(message, MessagesTimeLimit, SECONDS);
            return State.OK;
        }catch (InterruptedException e){
            e.printStackTrace();
            return State.ERROR;
        }
    }

    public void startToHold(String pairName){
        colonyDao.recordHolder(pairName);
        pairs.put(pairName, new AtomicInteger(0));
    }

    public State stopToHold(String pairName){
        try {
            pairs.remove(pairName);
            colonyDao.deleteHolder(pairName);
            return State.OK;
        }catch (Exception e){
            return State.ERROR;
        }
    }

    public State appendMessage(Message message){
        if(!healthy){
            System.out.println("NODE UNHEALTHY!");
            return State.ERROR;
        }
        if(message.getSenderId().equals(message.getReceiverId()))
            return State.ERROR;
        String pairName = message.getPairName();
        if(pairs.containsKey(pairName))
            return append(pairName, message);

        String holderAddress = colonyDao.getHolder(pairName);
        if(holderAddress == null || holderAddress.equals(colonyDao.getSelfAddress())) {
            startToHold(pairName);
            return append(pairName, message);
        }

        State result = colonyDao.forwardRequest(holderAddress, JSONObject.toJSONString(message), "/append");

        if(result == State.ERROR){
            startToHold(pairName);
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
            params.add("pairName", pairName);
            colonyDao.forwardRequest(holderAddress, params, "/stop");
            return append(pairName, message);
        }

        return result;
    }
}
