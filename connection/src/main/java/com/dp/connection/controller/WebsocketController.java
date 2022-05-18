package com.dp.connection.controller;

import com.dp.connection.entity.Message;
import com.dp.connection.service.WebsocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebsocketController {
    @PostMapping("/send")
    public String send(Long receiverId, String content){
        System.out.println("prepare to send to " + receiverId + " : " + content);
        return WebsocketService.distributeInfo(content, receiverId).toString();
    }
    @PostMapping("/disconnect")
    public String disconnect(Long userId){
        return WebsocketService.disconnect(userId).toString();
    }
}
