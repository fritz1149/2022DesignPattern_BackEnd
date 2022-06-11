package com.dp.chat.controller;

import com.alibaba.fastjson.JSONObject;
import com.dp.chat.dao.CacheDao;
import com.dp.chat.service.StaticService;
import com.dp.common.service.CheckService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContactController {
    @Autowired
    StaticService staticService;
    @Autowired
    CheckService checkService;

    @ApiOperation("加好友")
    @PostMapping("/add")
    public JSONObject addContact(@RequestParam Long userId, @RequestParam Long contactId){
        JSONObject ret = new JSONObject();
        try{
            ret.put("code", 200);
            ret.put("msg", staticService.addContact(userId, contactId));
            staticService.claimStorage(userId, contactId);
        }catch (Exception e){
            return ret.fluentPut("code", 500);
        }
        return ret;
    }

    @ApiOperation("请求加好友")
    @PostMapping("/request")
    public JSONObject requestContact(@ApiParam("自己id")@RequestParam Long userId,
                                     @ApiParam("对方id")@RequestParam Long contactId,
                                     @ApiParam("request表示请求加好友，response表示同意加好友")@RequestParam String type){
        JSONObject ret = new JSONObject();
        try{
            if(!type.equals("request") && !type.equals("response")) {
                ret.put("code", 400);
                ret.put("msg", "type参数不合法");
            }
            else if(!checkService.isValidUser(userId) || !checkService.isValidUser(contactId)){
                ret.put("code", 400);
                ret.put("msg", "不合法的用户id或对方id");
            }
            else if(staticService.beContact(userId, contactId, type))
                ret.put("code", 200);
            else {
                ret.put("code", 403);
                ret.put("msg", "已经是好友");
            }
        }catch (Exception e){
            e.printStackTrace();
            return ret.fluentPut("code", 500);
        }
        return ret;
    }

    @ApiOperation("删好友")
    @PostMapping("/remove")
    public JSONObject removeContact(@RequestParam Long userId, @RequestParam Long contactId){
        JSONObject ret = new JSONObject();
        try{
            if(!checkService.isValidUser(userId) || !checkService.isValidUser(contactId)){
                ret.put("code", 400);
                ret.put("msg", "不合法的用户id或对方id");
            }
            else if(staticService.removeContact(userId, contactId))
                ret.put("code", 200);
            else{
                ret.put("code", 403);
                ret.put("msg", "不是好友");
            }
        }catch (Exception e){
            return ret.fluentPut("code", 500);
        }
        return ret;
    }

    @ApiOperation("获取好友列表")
    @GetMapping("/get")
    public JSONObject getContacts(@RequestParam Long userId){
        JSONObject ret = new JSONObject();
        try{
            if(!checkService.isValidUser(userId)){
                ret.put("code", 400);
                ret.put("msg", "不合法的用户id");
            }
            else{
                ret.put("code", 200);
                ret.put("data", staticService.getContacts(userId));
            }
        }catch (Exception e){
            return ret.fluentPut("code", 500);
        }
        return ret;
    }

    @ApiOperation("设置好友备注")
    @PostMapping("/remark")
    public JSONObject uploadRemark(@RequestParam Long userId, @ApiParam("好友id") @RequestParam Long contactId,
                                 @ApiParam("备注") @RequestParam String remark){
        JSONObject ret = new JSONObject();
        try {
            if(!checkService.isValidUser(userId) || !checkService.isValidUser(contactId)){
                ret.put("code", 400);
                ret.put("msg", "不合法的用户id或对方id");
            }
            else if(staticService.setRemark(userId, contactId, remark).equals(1L)){
                ret.put("code", 200);
                ret.put("msg", "OK");
            }
            else{
                ret.put("code", 403);
                ret.put("msg", "非好友关系");
            }
        }catch (Exception e){
            ret.put("code", 500);
            ret.put("msg", "error");
        }
        return ret;
    }

    @ApiOperation("设置好友分组")
    @PostMapping("/group")
    public JSONObject uploadGroup(@RequestParam Long userId, @ApiParam("好友id") @RequestParam Long contactId,
                                   @ApiParam("分组") @RequestParam String group){
        JSONObject ret = new JSONObject();
        try {
            if(!checkService.isValidUser(userId) || !checkService.isValidUser(contactId)){
                ret.fluentPut("code", 400)
                    .fluentPut("msg", "不合法的用户id或对方id");
            }
            else if(staticService.setGroup(userId, contactId, group).equals(1L)){
                ret.fluentPut("code", 200)
                    .fluentPut("msg", "OK");
            }
            else{
                ret.fluentPut("code", 403)
                    .fluentPut("msg", "非好友关系");
            }
        }catch (Exception e){
            ret.put("code", 500);
            ret.put("msg", "error");
        }
        return ret;
    }
}
