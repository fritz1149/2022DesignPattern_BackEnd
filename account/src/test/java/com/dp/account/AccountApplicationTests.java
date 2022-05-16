package com.dp.account;

import com.dp.account.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AccountApplicationTests {
    @Autowired
    UserService userService;

    @Test
    void contextLoads() {
    }
    @Test
    void loginTest(){
        System.out.println(userService.loginCheck(1L, "admin"));
    }

}
