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
    public Boolean close(){
        try {
            session.close();
            return true;
        }catch (IOException e){
            System.out.println("关连接失败了");
            return false;
        }
    }
    @OnMessage
    public void onMessage(String msg){
        System.out.println("get msg from " + userId + ": " + msg);
    }

    public void sendInfo(Object data) throws IOException{
        session.getBasicRemote().sendText(JSONObject.toJSONString(data));
    }
    public enum State {
        OK, FORWARD_OK, USER_NOT_FOUND, ERROR
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
        if (webSocketServices.containsKey(userId)) {
            if(webSocketServices.get(userId).close())
                return State.OK;
            return State.ERROR;
        }
        else{
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
            params.add("userId", userId);
            return colonyService.forwardRequest(userId, params, "/disconnect");
        }
    }
}
