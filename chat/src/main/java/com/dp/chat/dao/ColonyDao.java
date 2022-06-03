package com.dp.chat.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.dp.common.Enum.State;

@Repository
public class ColonyDao {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${chat-address}")
    private String selfAddress;

    public void recordHolder(String pairName){
        redisTemplate.opsForValue().set(pairName, selfAddress);
    }
    public void deleteHolder(String pairName){
        String holder = getHolder(pairName);
        if(holder != null && holder.equals(selfAddress))
            redisTemplate.delete(pairName);
    }
    public String getHolder(String pairName){
        return (String)redisTemplate.opsForValue().get(pairName);
    }
    public String getSelfAddress(){return selfAddress;}
    public State forwardRequest(String goalAddress, Object params, String request){
        System.out.println("goalAddress: " + goalAddress);
        State ret = State.ERROR;
        try {
            ret = State.valueOf(restTemplate.postForObject("http://" + goalAddress + request, params, String.class));
        }catch (Exception e){
            e.printStackTrace();
            return State.ERROR;
        }
        if(ret == State.OK)
            return State.FORWARD_OK;
        return ret;
    }
}
