package com.dp.account.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@FeignClient("connection")
public interface RemoteService {
    @PostMapping("/disconnect")
    public String kickOut(@RequestParam Long userId);
}
