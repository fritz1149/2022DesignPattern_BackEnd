package com.dp.chat.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dp.chat.entity.Message;
import com.dp.chat.service.AppendService;
import com.dp.chat.service.DistributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {
    @Autowired
    AppendService appendService;
    @Autowired
    DistributeService distributeService;

    @PostMapping("/append")
    public String appendRawMessage(@RequestBody String rawMessage){
        System.out.println("message: " + rawMessage);
        Message message;
        try {
            message = new Message(rawMessage);
        }catch (Exception e){
            e.printStackTrace();
            return "ERROR";
        }
        return appendService.appendMessage(message).toString();
    }

    @PostMapping("/ack")
    public String receiveAck(@RequestParam Long userId, @RequestParam Long clientLatestId){
        distributeService.popPushList(userId, clientLatestId);
        return "OK";
    }

    @PostMapping("/data")
    public String receiveRawData(@RequestBody String rawData){
        System.out.println("receive: " + rawData);
        JSONObject json = JSON.parseObject(rawData);
        String type = (String)(json.get("type"));
        Assert.notNull(type, "type null !");
        if(type.equals("message"))
            return appendRawMessage(json.get("message").toString());
        if(type.equals("ack"))
            return receiveAck(Long.valueOf(json.get("userId").toString()), Long.valueOf(json.get("latestId").toString()));
        return "ERROR";
    }

    @PostMapping("/stop")
    public String stopHolding(@RequestParam String pairName){
        System.out.println("stop holding: " + pairName);
        return appendService.stopToHold(pairName).toString();
    }

}
