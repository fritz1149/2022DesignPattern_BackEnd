package com.dp.chat.service;

import com.alibaba.fastjson.JSONObject;
import com.dp.chat.entity.Message;
import com.dp.common.Enum.State;
import com.dp.common.Name;
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
    private ColonyService colonyService;
    @Autowired
    private DistributeService distributeService;
    public ArrayBlockingQueue<Message> messages = new ArrayBlockingQueue<Message>(MessagesNumberLimit);
    private Boolean healthy = true;

    public static int csCount = 0;
    public AppendService(){
        csCount++;
        System.out.println("chat service created " + csCount);
        Runnable workload = new Runnable(){
            @Override
            public void run() {
                while(true){
                    Message message = null;
                    try {
                         message = messages.take();
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                    String pairName = message.getPairName();
                    if(!pairs.containsKey(pairName))
                        continue;
                    distributeService.saveToStorage(message);
                    distributeService.saveToPushList(message);
                    if (pairs.get(pairName).decrementAndGet() <= 0)
                        stopToHold(pairName);
                }
                healthy = false;
            }
        };
        Thread work = new Thread(workload);
        if(csCount == 1)
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
        colonyService.recordHolder(pairName);
        pairs.put(pairName, new AtomicInteger(0));
    }

    public State stopToHold(String pairName){
        try {
            pairs.remove(pairName);
            colonyService.deleteHolder(pairName);
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

        String pairName = message.getPairName();
        if(pairs.containsKey(pairName))
            return append(pairName, message);

        String holderAddress = colonyService.getHolder(pairName);
        System.out.println("holderAddress: " + holderAddress + "\nselfAddress: " + colonyService.getSelfAddress());
        if(holderAddress == null || holderAddress.equals(colonyService.getSelfAddress())) {
            startToHold(pairName);
            return append(pairName, message);
        }

        State result = colonyService.forwardRequest(holderAddress, JSONObject.toJSONString(message), "/append");

        if(result == State.ERROR){
            startToHold(pairName);
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
            params.add("pairName", pairName);
            colonyService.forwardRequest(holderAddress, params, "/stop");
            return append(pairName, message);
        }

        return result;
    }
}
