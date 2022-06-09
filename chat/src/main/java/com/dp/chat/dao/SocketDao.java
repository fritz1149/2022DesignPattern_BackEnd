package com.dp.chat.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dp.common.Enum.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SocketDao {
    @Autowired
    RemoteDao remoteDao;
    public State sendMessages(Long userId, Long serverLatestId, JSONArray messages){
        JSONObject data = new JSONObject();
        data.put("messages", messages);
        data.put("type", "message");
        data.put("start", serverLatestId + 1);
        System.out.println("prepare to send: " + data.toJSONString());
        return State.valueOf(remoteDao.send(userId, data.toJSONString()));
    }

    public State sendGroupMessages(Long userId, Long serverLatestId, Long groupId, JSONArray messages){
        JSONObject data = new JSONObject();
        data.put("messages", messages);
        data.put("type", "groupMessage");
        data.put("groupId", groupId);
        data.put("start", serverLatestId + 1);
        System.out.println("prepare to send: " + data.toJSONString());
        return State.valueOf(remoteDao.send(userId, data.toJSONString()));
    }

    public void sendInfo(Long userId, Long serverLatestId, String msg){
        JSONObject data = new JSONObject();
        data.put("type", "info");
        data.put("latestId", serverLatestId);
        data.put("msg", msg);
        System.out.println("prepare to send: " + data.toJSONString());
        remoteDao.send(userId, data.toJSONString());
    }
}
