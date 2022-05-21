package com.dp.connection.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import com.dp.common.Enum.State;
@Service
@ServerEndpoint("/ws/{userId}")
public class WebsocketService {
    private Long userId;
    private Session session;
    private static final ConcurrentHashMap<Long, WebsocketService> webSocketServices=new ConcurrentHashMap<>();
    private static final AtomicInteger onlineCount = new AtomicInteger();
    private static ColonyService colonyService;
    @Autowired
    public void setColonyService(ColonyService cs){
        colonyService = cs;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) {
        this.userId = userId;
        this.session = session;
        webSocketServices.put(userId, this);
        onlineCount.incrementAndGet();
        System.out.println("session open. online: " + onlineCount + " userId: " + userId);
        colonyService.recordConnect(userId);
    }
    @OnClose
    public void onClose(){
        webSocketServices.remove(userId);
        onlineCount.decrementAndGet();
        System.out.println("session close. online: " + onlineCount + " userId: " + userId);
        colonyService.deleteConnect(userId);
    }
    public void close() throws IOException{
        session.close();
    }
    @OnMessage
    public void onMessage(String msg){
        System.out.println("get msg from " + userId + ": " + msg);
        colonyService.sendToChat(msg);
    }

    public void sendInfo(Object data) throws IOException{
        session.getBasicRemote().sendText(JSONObject.toJSONString(data));
    }
    public static State distributeInfo(Object data, Long receiverId){
        try {
            if (webSocketServices.containsKey(receiverId)) {
                webSocketServices.get(receiverId).sendInfo(data);
                return State.OK;
            }
            else {
                MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
                params.add("receiverId", receiverId);
                params.add("content", data);
                return colonyService.forwardRequest(receiverId, params, "/send");
            }
        }catch (IOException e){
            return State.ERROR;
        }
    }

    public static State disconnect(Long userId){
        if(userId == null)
            return State.ERROR;
        if (webSocketServices.containsKey(userId)) {
            WebsocketService ws = webSocketServices.get(userId);
            webSocketServices.remove(userId);
            try{
                ws.sendInfo("您的账号刚刚在另一台设备上登录，因此断开连接");
                ws.close();
                return State.OK;
            }catch (IOException e){
                e.printStackTrace();
                return State.ERROR;
            }
        }
        else{
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
            params.add("userId", userId);
            return colonyService.forwardRequest(userId, params, "/disconnect");
        }
    }
}
