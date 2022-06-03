package com.dp.account.controller;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson.JSONObject;
import com.dp.account.service.RemoteService;
import com.dp.account.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountController {
    @Autowired
    UserService userService;
    @Autowired
    RemoteService remoteService;

    @GetMapping("/hello")
    public String hello(){
        String ret = "hello\n";
        Boolean isLogin = StpUtil.isLogin();
        ret += "\nisLogin: " + isLogin;
        if(isLogin)
            ret += "\nuserId: " + StpUtil.getLoginId();
        return ret;
    }
    @ApiOperation("携带token才能登出")
    @PostMapping("/logout")
    public SaResult logout(){
        Long userId = (Long) StpUtil.getLoginId();
        userService.logout(userId);
        StpUtil.logout();
        return SaResult.ok(remoteService.kickOut(userId));
    }

    @PostMapping("/login")
    public SaResult login(@RequestParam Long userId, @RequestParam String password){
        switch (userService.loginCheck(userId, password)){
            case LOGIN_OK:
                StpUtil.login(userId, "PC");
                String token = StpUtil.getTokenValueByLoginId(userId, "PC");
                remoteService.kickOut(userId);
                return new SaResult(200, "登陆成功", new JSONObject().fluentPut("token", token));
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
            return new SaResult(200, "注册成功", new JSONObject().fluentPut("userId", userId));
        }catch (Exception e){
            e.printStackTrace();
            return SaResult.error("后端寄了");
        }
    }

}
