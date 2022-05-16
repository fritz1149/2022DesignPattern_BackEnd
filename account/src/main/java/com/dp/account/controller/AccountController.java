package com.dp.account.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.dp.account.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountController {
    @Autowired
    UserService userService;

    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

    @PostMapping("/login")
    public SaResult login(@RequestParam Long userId, @RequestParam String password){
        switch (userService.loginCheck(userId, password)){
            case LOGIN_OK:
                String device = StpUtil.getLoginDevice();
                StpUtil.login(userId, device);
                String token = StpUtil.getTokenValueByLoginId(userId, device);
                return new SaResult(200, "登陆成功", token);
            case USER_NOT_FOUND:
                return new SaResult(403, "用户不存在", null);
            case PWD_ERROR:
                return new SaResult(403, "密码错误", null);
            default:
                return SaResult.error("不应该出现的错误，请殴打后端同学");
        }
    }
    @PostMapping("/register")
    public SaResult register(@RequestParam String userName, @RequestParam String password){
        try{
            Long userId = userService.register(userName, password);
            return new SaResult(200, "注册成功", userId);
        }catch (Exception e){
            e.printStackTrace();
            return SaResult.error("后端寄了");
        }
    }

    @PostMapping("/logout")
    public SaResult logout(@RequestParam Long userId){
        StpUtil.logout(userId);
        return SaResult.ok();
    }
}
