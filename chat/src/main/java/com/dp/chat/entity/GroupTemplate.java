package com.dp.chat.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupTemplate {
    int number;
    List messages;
    List members;
    String pair_name;

    public GroupTemplate(int number, String pair_name, Long[] userIds) {
        this.number = number;
        this.pair_name = pair_name;
        messages = new ArrayList<>();
        members = new ArrayList<>();
        Collections.addAll(members, userIds);
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List getMessages() {
        return messages;
    }

    public void setMessages(List messages) {
        this.messages = messages;
    }

    public List getMembers() {
        return members;
    }

    public void setMembers(List members) {
        this.members = members;
    }

    public String getPair_name() {
        return pair_name;
    }

    public void setPair_name(String pair_name) {
        this.pair_name = pair_name;
    }
}
