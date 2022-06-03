package com.dp.chat.entity;

import java.util.ArrayList;
import java.util.List;

public class ChatTemplate {
    int number;
    List messages;
    String pair_name;

    public ChatTemplate(int number, String pair_name) {
        this.number = number;
        this.messages = new ArrayList<>();
        this.pair_name = pair_name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public String getPair_name() {
        return pair_name;
    }

    public void setPair_name(String pair_name) {
        this.pair_name = pair_name;
    }
}
