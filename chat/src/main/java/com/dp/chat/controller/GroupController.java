package com.dp.chat.controller;

import com.alibaba.fastjson.JSONObject;
import com.dp.chat.dao.GroupMapper;
import com.dp.chat.service.StaticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GroupController {
    @Autowired
    StaticService staticService;
    @PostMapping("/create")
    public JSONObject createGroup(@RequestParam String groupName, @RequestParam Long userId){
        return new JSONObject()
                .fluentPut("code", 200)
                .fluentPut("msg", staticService.claimGroup(groupName, userId))
                ;
    }
    @PostMapping("/join")
    public JSONObject joinGroup(@RequestParam Long userId, @RequestParam Long groupId){
        return new JSONObject()
                .fluentPut("code", 200)
                .fluentPut("msg", staticService.joinGroup(userId, groupId))
                ;
    }
    @PostMapping("/leave")
    public JSONObject leaveGroup(@RequestParam Long userId, @RequestParam Long groupId){
        return new JSONObject()
                .fluentPut("code", 200)
                .fluentPut("msg", staticService.leaveGroup(userId, groupId))
                ;
    }


}
