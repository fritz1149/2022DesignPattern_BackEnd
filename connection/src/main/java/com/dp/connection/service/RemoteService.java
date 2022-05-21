package com.dp.connection.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@FeignClient("chat")
public interface RemoteService {
    @PostMapping("/data")
    public String send(@RequestBody String rawData);
}
