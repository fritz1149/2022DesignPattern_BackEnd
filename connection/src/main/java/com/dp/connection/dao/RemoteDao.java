package com.dp.connection.dao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Repository
@FeignClient("chat")
public interface RemoteDao {
    @PostMapping("/data")
    public String send(@RequestBody String rawData);
}
