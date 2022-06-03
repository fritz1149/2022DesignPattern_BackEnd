package com.dp.chat.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dp.chat.entity.Ack;
import com.dp.chat.entity.Message;
import com.dp.chat.service.AppendService;
import com.dp.chat.service.DistributeServiceGroupImpl;
import com.dp.chat.service.DistributeServiceSingleImpl;
import com.dp.chat.service.StaticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
public class ChatController {
    @Autowired
    AppendService appendService;
    @Autowired
    DistributeServiceSingleImpl distributeServiceSingle;
    @Autowired
    DistributeServiceGroupImpl distributeServiceGroup;
    @Autowired
    StaticService staticService;

    @ApiIgnore
    @PostMapping("/data")
    public String receiveRawData(@RequestBody String rawData){
        JSONObject json = JSON.parseObject(rawData);
        String type = (String)(json.get("type"));
        if(type.equals("message")) {
            Message message = new Message(json.get("message").toString());
            message.setDs(distributeServiceSingle);
            return appendService.appendMessage(message).toString();
        }
        if(type.equals("ack")) {
            Ack ack = new Ack(Long.valueOf(json.get("userId").toString()), Long.valueOf(json.get("latestId").toString()));
            ack.setDs(distributeServiceSingle);
            ack.getDs().receiveAck(ack);
            return "OK";
        }
        return "ERROR";
    }

    @ApiIgnore
    @PostMapping("/stop")
    public String stopHolding(@RequestParam String pairName){
        System.out.println("stop holding: " + pairName);
        return appendService.stopToHold(pairName).toString();
    }

    @PostMapping("/startChat")
    public JSONObject startChat(@RequestParam Long userId1, @RequestParam Long userId2){
        return new JSONObject()
                .fluentPut("code", 200)
                .fluentPut("msg", staticService.claimStorage(userId1, userId2));
    }

    @GetMapping("/log")
    public JSONObject getLog(@RequestParam Long userId1, @RequestParam Long userId2,
                         @RequestParam Integer pageSize, @RequestParam Integer pageNum){
        return new JSONObject()
                .fluentPut("code", 200)
                .fluentPut("data", staticService.getChatLog(userId1, userId2, pageSize, pageNum).toJSONString());
    }
}
