package com.dp.chat.controller;

import com.alibaba.fastjson.JSONObject;
import com.dp.chat.dao.CacheDao;
import com.dp.chat.service.StaticService;
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

    @ApiOperation("删好友")
    @PostMapping("/remove")
    public JSONObject removeContact(@RequestParam Long userId, @RequestParam Long contactId){
        JSONObject ret = new JSONObject();
        try{
            ret.put("code", 200);
            ret.put("msg", staticService.removeContact(userId, contactId));
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
            ret.put("code", 200);
            ret.put("data", staticService.getContacts(userId));
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
            if(staticService.setRemark(userId, contactId, remark).equals(1L)){
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
            if(staticService.setGroup(userId, contactId, group).equals(1L)){
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
}
