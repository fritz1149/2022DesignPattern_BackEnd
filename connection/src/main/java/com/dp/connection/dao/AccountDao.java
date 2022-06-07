package com.dp.connection.dao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Repository
@FeignClient("account")
public interface AccountDao {
    @PostMapping("/logoutInner")
    public String logout(@RequestParam Long userId);
}
