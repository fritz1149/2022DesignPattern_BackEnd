package com.dp.chat.dao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Repository
@FeignClient("connection")
public interface RemoteDao {
    @PostMapping("/send")
    public String send(@RequestParam Long receiverId, @RequestParam String content);
}
