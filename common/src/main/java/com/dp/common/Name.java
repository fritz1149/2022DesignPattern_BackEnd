package com.dp.common;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class Name {
    public Name(){};

    public static String socketName(Long userId){
        if(userId == null)
            throw new IllegalArgumentException();
        return "socket" + userId;
    }

    public static String pairName(Long userId1, Long userId2){
        if(userId1 == null || userId2 == null)
            throw new IllegalArgumentException();
        if (userId1 > userId2) {
            Long t = userId1;
            userId1 = userId2;
            userId2 = t;
        }
        return userId1 + "_" + userId2;
    }

    public static String listName(Long userId){
        Assert.notNull(userId, "userId null !");
        return "list" + userId;
    }

    public static String groupListName(Long groupId){
        Assert.notNull(groupId, "groupId null !");
        return "groupList" + groupId;
    }

    public static String stateName(Long userId){
        Assert.notNull(userId, "userId null !");
        return "state" + userId;
    }

    public static String lastId(Long userId){
        Assert.notNull(userId, "userId null !");
        return "last" + userId;
    }

    public static String groupOnlineList(Long groupId){
        return "online" + groupId;
    }
}
