package com.dp.chat.controller;

import com.alibaba.fastjson.JSONObject;
import com.dp.chat.dao.GroupMapper;
import com.dp.chat.service.StaticService;
import com.dp.chat.service.searchService.AllSearch;
import com.dp.chat.service.searchService.MyGroupSearch;
import com.dp.chat.service.searchService.SearchService;
import com.dp.chat.service.searchService.StrangerSearch;
import com.dp.common.service.CheckService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GroupController {
    @Autowired
    StaticService staticService;
    @Autowired
    AllSearch allSearch;
    @Autowired
    MyGroupSearch myGroupSearch;
    @Autowired
    StrangerSearch strangerSearch;
    @Autowired
    CheckService checkService;

    @ApiOperation("创建群组")
    @PostMapping("/create")
    public JSONObject createGroup(@RequestParam String groupName, @RequestParam Long userId){
        JSONObject ret = new JSONObject();
        try{
            if(!checkService.isValidUser(userId))
                ret.fluentPut("code", 400)
                    .fluentPut("msg", "不合法用户id");
            else
                ret.fluentPut("code", 200)
                    .fluentPut("data",
                        new JSONObject().fluentPut("groupId", staticService.claimGroup(groupName, userId))
                    );
        }catch (Exception e){
            e.printStackTrace();
            ret.put("code", 500);
        }
        return ret;
    }

    @ApiOperation("获取加入的群组列表")
    @GetMapping("/group")
    public JSONObject getGroups(@RequestParam Long userId){
        JSONObject ret = new JSONObject();
        try{
            if(!checkService.isValidUser(userId))
                ret.fluentPut("code", 400)
                        .fluentPut("msg", "不合法用户id");
            else
                ret.fluentPut("code", 200)
                        .fluentPut("data", staticService.getMyGroups(userId));
        }catch (Exception e){
            e.printStackTrace();
            ret.put("code", 500);
        }
        return ret;
    }

    @ApiOperation("获取群组信息")
    @GetMapping("/info")
    public JSONObject getInfo(@ApiParam("搜索范围，可选的值：all, my, stranger")@RequestParam String scope,
                              @ApiParam("搜索类型，可选的值：id, name")@RequestParam String key,
                              @ApiParam("关键词")@RequestParam String value,
                              @ApiParam("自己id")@RequestParam Long userId){
        JSONObject ret = new JSONObject();
        SearchService searchService;
        switch (scope){
            case "all":
                searchService = allSearch; break;
            case "my":
                searchService = myGroupSearch; break;
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
                    data = searchService.getGroupByName(value, userId);
                    break;
                case "id":
                    data = staticService.getGroup(Long.valueOf(value));
                    if(data == null){
                        ret.put("code", 403);
                        ret.put("msg", "cannot find group");
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

    @ApiOperation("加入群组")
    @PostMapping("/join")
    public JSONObject joinGroup(@RequestParam Long userId, @RequestParam Long groupId){
        JSONObject ret = new JSONObject();
        try{
            if(!checkService.isValidUser(userId))
                ret.fluentPut("code", 400)
                        .fluentPut("msg", "不合法用户id");
            else if(!checkService.isValidGroup(groupId))
                ret.fluentPut("code", 400)
                        .fluentPut("msg", "不合法群组id");
            else
                ret.fluentPut("code", 200)
                        .fluentPut("data", staticService.joinGroup(userId, groupId));
        }catch (Exception e){
            e.printStackTrace();
            ret.put("code", 500);
        }
        return ret;
    }

    @ApiOperation("离开群组")
    @PostMapping("/leave")
    public JSONObject leaveGroup(@RequestParam Long userId, @RequestParam Long groupId){
        JSONObject ret = new JSONObject();
        try{
            if(!checkService.isValidUser(userId))
                ret.fluentPut("code", 400)
                        .fluentPut("msg", "不合法用户id");
            else if(!checkService.isValidGroup(groupId))
                ret.fluentPut("code", 400)
                        .fluentPut("msg", "不合法群组id");
            else if(staticService.leaveGroup(userId, groupId))
                ret.fluentPut("code", 200);
            else
                ret.fluentPut("code", 403)
                        .fluentPut("msg", "用户不在群组中");
        }catch (Exception e){
            e.printStackTrace();
            ret.put("code", 500);
        }
        return ret;
    }

    @ApiOperation("上传新头像")
    @PostMapping("avatar")
    public JSONObject uploadAvatar(@RequestParam Long groupId, @RequestParam String avatar){
        JSONObject ret = new JSONObject();
        try {
            if(!checkService.isValidGroup(groupId))
                ret.fluentPut("code", 400)
                        .fluentPut("msg", "不合法群组id");
            else if(!checkService.isValidFile(avatar))
                ret.fluentPut("code", 400)
                        .fluentPut("msg", "不合法文件路径");
            else if(staticService.updateGroupAvatar(groupId, avatar)){
                ret.put("code", 200);
                ret.put("msg", "OK");
            }
            else{
                ret.put("code", 403);
                ret.put("msg", "群组不存在");
            }
        }catch (Exception e){
            ret.put("code", 500);
            ret.put("msg", e.getMessage());
        }
        return ret;
    }
}
