package com.dp.connection.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static com.dp.connection.service.WebsocketService.State;

@Service
public class ColonyService {
    @Autowired
    private RedisTemplate redisTemplate = new RedisTemplate<>();
    @Autowired
    private RestTemplate restTemplate = new RestTemplate();
    @Value("${socket-address}")
    private String selfAddress;

    public void recordConnect(Long userId){
        redisTemplate.opsForValue().set("socket" + userId, selfAddress);
    }
    public void deleteConnect(Long userId){
        redisTemplate.delete("socket" + userId);
    }
    public State forwardRequest(Long userId, MultiValueMap<String, Object> params, String request){
        String goalAddress = (String)redisTemplate.opsForValue().get("socket" + userId);
        if(goalAddress == null)
            return State.USER_NOT_FOUND;
        State ret = State.valueOf(restTemplate.postForObject("http://" + goalAddress + request, params, String.class));
        if(ret == State.OK)
            return State.FORWARD_OK;
        return ret;
    }
}
