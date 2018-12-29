package com.braumsolutions.advogadoresponde.Model;

public class ChatMessage {

    private String uid;
    private String name;
    private String message;
    private String key;

    public ChatMessage() {

    }

    public ChatMessage(String uid, String name, String message, String key) {
        this.uid = uid;
        this.name = name;
        this.message = message;
        this.key = key;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String sender) {
        this.uid = sender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
