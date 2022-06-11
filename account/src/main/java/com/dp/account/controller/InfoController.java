package com.dp.account.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSONObject;
import com.dp.account.entity.User;
import com.dp.account.service.UserService;
import com.dp.account.service.searchService.AllSearch;
import com.dp.account.service.searchService.ContactSearch;
import com.dp.account.service.searchService.SearchService;
import com.dp.account.service.searchService.StrangerSearch;
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
    @Autowired
    AllSearch allSearch;
    @Autowired
    ContactSearch contactSearch;
    @Autowired
    StrangerSearch strangerSearch;


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
    public JSONObject getInfo(@ApiParam("搜索范围，可选的值：all, contact, stranger")@RequestParam String scope,
                              @ApiParam("搜索类型，可选的值：id, name")@RequestParam String key,
                              @ApiParam("关键词")@RequestParam String value){
        JSONObject ret = new JSONObject();
        SearchService searchService;
        switch (scope){
            case "all":
                searchService = allSearch; break;
            case "contact":
                searchService = contactSearch; break;
            case "stranger":
                searchService = strangerSearch; break;
            default:
                ret.put("code", 400);
                ret.put("msg", "wrong scope");
                return ret;
        }

        Object data;
        try {
            switch (key) {
                case "name":
                    Long userId = Long.valueOf(StpUtil.getLoginId().toString());
                    data = searchService.getUserByName(value, userId);
                    break;
                case "id":
                    data = userService.getInfo(Long.valueOf(value));
                    if(data == null){
                        ret.put("code", 403);
                        ret.put("msg", "cannot find user");
                    }
                    break;
                default:
                    ret.put("code", 400);
                    ret.put("msg", "wrong key");
                    return ret;
            }
            ret.put("code", 200);
            ret.put("data", data);
        }catch (Exception e){
            ret.put("code", 500);
            ret.put("msg", e.getMessage());
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

    @ApiOperation("上传个人用户名")
    @PostMapping("name")
    public JSONObject uploadName(@ApiParam("新名字") @RequestParam String name){
        JSONObject ret = new JSONObject();
        try {
            Long userId = Long.valueOf(StpUtil.getLoginId().toString());
            if(userService.uploadName(userId, name).equals(1L)){
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
