package com.dp.account.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSONObject;
import com.dp.account.entity.User;
import com.dp.account.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class InfoController {
    @Autowired
    UserService userService;

    @ApiOperation("上传新头像")
    @PostMapping("avatar")
    public JSONObject uploadAvatar(@RequestParam String avatarPath){
        JSONObject ret = new JSONObject();
        try {
            Long userId = Long.valueOf(StpUtil.getLoginId().toString());
            if(userService.uploadAvatar(userId, avatarPath).equals(1L)){
                ret.put("code", 200);
                ret.put("msg", "OK");
            }
            else{
                ret.put("code", 403);
                ret.put("msg", "用户不存在");
            }
        }catch (Exception e){
            ret.put("code", 500);
            ret.put("msg", e.getMessage());
        }
        return ret;
    }

    @ApiOperation("上传新密码")
    @PostMapping("password")
    public JSONObject uploadPassword(@RequestParam String password){
        JSONObject ret = new JSONObject();
        try {
            Long userId = Long.valueOf(StpUtil.getLoginId().toString());
            if(userService.uploadPassword(userId, password).equals(1L)){
                ret.put("code", 200);
                ret.put("msg", "OK");
            }
            else{
                ret.put("code", 403);
                ret.put("msg", "用户不存在");
            }
        }catch (Exception e){
            ret.put("code", 500);
            ret.put("msg", e.getMessage());
        }
        return ret;
    }

    @ApiOperation("获取用户信息")
    @GetMapping("info")
    public JSONObject getInfo(@ApiParam("搜索类型，可选的值：id, name") String key,
                              @ApiParam("关键词")String value){
        JSONObject ret = new JSONObject();
        Object data;
        try {
            switch (key) {
                case "name":
                    data = userService.getUserByName(value);
                    break;
                case "id":
                    data = userService.getInfo(Long.valueOf(value));
                    if(data == null){
                        ret.put("code", 403);
                        ret.put("msg", "cannot find user");
                        return ret;
                    }
                    break;
                default:
                    ret.put("code", 400);
                    ret.put("msg", "wrong key");
                    return ret;
            }
        }catch (Exception e){
            ret.put("code", 500);
            ret.put("msg", e.getMessage());
            return ret;
        }
        ret.put("code", 200);
        ret.put("data", data);
        return ret;
    }

    @ApiOperation("获取陌生用户信息")
    @GetMapping("strangerInfo")
    public JSONObject getStrangerInfo(@ApiParam("搜索类型，可选的值：目前只有name") String key,
                                      @ApiParam("关键词")String value){
        JSONObject ret = new JSONObject();
        if(key.equals("name")){
            try{
                Long userId = Long.valueOf(StpUtil.getLoginId().toString());
                List data = userService.getStrangerByName(value, userId);
                ret.put("code", 200);
                ret.put("data", data);
            }catch (Exception e){
                ret.put("code", 500);
                ret.put("msg", e.getMessage());
            }
        }
        else{
            ret.put("code", 400);
            ret.put("msg", "wrong key");
        }
        return ret;
    }

    @ApiOperation("上传个人签名")
    @PostMapping("sign")
    public JSONObject uploadSign(@ApiParam("签名") @RequestParam String sign){
        JSONObject ret = new JSONObject();
        try {
            Long userId = Long.valueOf(StpUtil.getLoginId().toString());
            if(userService.uploadField(userId, sign).equals(1L)){
                ret.put("code", 200);
                ret.put("msg", "OK");
            }
            else{
                ret.put("code", 403);
                ret.put("msg", "用户不存在");
            }
        }catch (Exception e){
            ret.put("code", 500);
            ret.put("msg", e.getMessage());
        }
        return ret;
    }
}
