package com.dp.connection.service;

import com.dp.common.Name;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.dp.common.Enum.State;

@Service
public class ColonyService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RemoteService remoteService;
    @Value("${socket-address}")
    private String selfAddress;

    public void recordConnect(Long userId){
        redisTemplate.opsForValue().set(Name.socketName(userId), selfAddress);
    }
    public void deleteConnect(Long userId){
        redisTemplate.delete(Name.socketName(userId));
    }
    public State forwardRequest(Long userId, MultiValueMap<String, Object> params, String request){
        String goalAddress = (String)redisTemplate.opsForValue().get(Name.socketName(userId));
        if(goalAddress == null)
            return State.TARGET_NOT_FOUND;
        State ret = State.valueOf(restTemplate.postForObject("http://" + goalAddress + request, params, String.class));
        if(ret == State.OK)
            return State.FORWARD_OK;
        return ret;
    }
    public void sendToChat(String msg){
        remoteService.send(msg);
    }
}
