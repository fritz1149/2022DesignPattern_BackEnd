package com.dp.connection.controller;

import com.dp.connection.service.WebsocketService;
import org.springframework.web.bind.annotation.*;

@RestController
public class WebsocketController {
    @PostMapping("/send")
    public String send(@RequestParam Long receiverId, @RequestParam String content){
        System.out.println("prepare to send to " + receiverId + " : " + content);
        return WebsocketService.distributeInfo(content, receiverId).toString();
    }
    @PostMapping("/disconnect")
    public String disconnect(@RequestParam Long userId){
        System.out.println("prepare to kick out " + userId);
        return WebsocketService.disconnect(userId).toString();
    }
}
