package com.dp.chat.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dp.chat.entity.Ack;
import com.dp.chat.entity.Message.Message;
import com.dp.chat.entity.Message.MessageFactory;
import com.dp.chat.service.AppendService;
import com.dp.chat.service.DistributeService;
import com.dp.chat.service.StaticService;
import com.dp.common.Name;
import com.dp.common.service.CheckService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
public class ChatController {
    @Autowired
    AppendService appendService;
    @Autowired
    DistributeService distributeService;
    @Autowired
    StaticService staticService;
    @Autowired
    MessageFactory messageFactory;
    @Autowired
    CheckService checkService;

    @ApiIgnore
    @PostMapping("/data")
    public String receiveRawData(@RequestBody String rawData){
        JSONObject json = JSON.parseObject(rawData);
        String type = (String)(json.get("type"));
        System.out.println("get msg" + rawData);
        if(type == null || type.equals("message") || type.equals("request") || type.equals("response")) {
            Message message = messageFactory.parseRaw(json.get("message").toString());
            return appendService.appendMessage(message).toString();
        }
        if(type.equals("ack")) {
            Ack ack = new Ack(Long.valueOf(json.get("userId").toString()), Long.valueOf(json.get("latestId").toString()));
            if(json.get("groupId") != null) {
                ack.setTargetId(Long.valueOf(json.get("groupId").toString()));
                distributeService.receiveAckGroup(ack);
            }
            else
                distributeService.receiveAckSingle(ack);
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

    @ApiIgnore
    @PostMapping("/append")
    public String append(@RequestBody String rawMessage){
        System.out.println("forward append: " + rawMessage);
        return receiveRawData(rawMessage);
    }

    @ApiOperation("???????????????????????????????????????")
    @PostMapping("/startChat")
    public JSONObject startChat(@ApiParam("??????id") @RequestParam Long userId1, @ApiParam("??????id") @RequestParam Long userId2){
        return new JSONObject()
                .fluentPut("code", 200)
                .fluentPut("msg", staticService.claimStorage(userId1, userId2));
    }

    @ApiOperation("??????????????????")
    @GetMapping("/singleLog")
    public JSONObject getLog(@ApiParam("??????id") @RequestParam Long userId1,
                             @ApiParam("??????id") @RequestParam Long userId2,
                             @ApiParam("???????????????") @RequestParam Integer pageSize,
                             @ApiParam("?????????") @RequestParam Integer pageNum){
        JSONObject ret = new JSONObject();
        try{
            if(!checkService.isValidUser(userId1) || !checkService.isValidUser(userId2))
                ret.fluentPut("code", 400)
                        .fluentPut("msg", "???????????????id");
            else
                ret.fluentPut("code", 200)
                        .fluentPut("data", staticService.getChatLog(Name.pairName(userId1, userId2), pageSize, pageNum).toJSONString());
        }catch (Exception e){
            e.printStackTrace();
            ret.fluentPut("code", 500);
        }
        return ret;
    }

    @ApiOperation("??????????????????")
    @GetMapping("/groupLog")
    public JSONObject getLog2(@ApiParam("??????id") @RequestParam Long groupId,
                             @ApiParam("???????????????") @RequestParam Integer pageSize,
                             @ApiParam("?????????") @RequestParam Integer pageNum){
        JSONObject ret = new JSONObject();
        try{
            if(!checkService.isValidGroup(groupId))
                ret.fluentPut("code", 400)
                        .fluentPut("msg", "???????????????id");
            else
                ret.fluentPut("code", 200)
                        .fluentPut("data", staticService.getChatLog(groupId.toString(), pageSize, pageNum).toJSONString());
        }catch (Exception e){
            e.printStackTrace();
            ret.fluentPut("code", 500);
        }
        return ret;
    }
}
