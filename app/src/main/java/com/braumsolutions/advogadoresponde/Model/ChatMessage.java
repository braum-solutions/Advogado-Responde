package com.braumsolutions.advogadoresponde.Model;

public class ChatMessage {

    private String uid;
    private String name;
    private String message;

    public ChatMessage() {

    }

    public ChatMessage(String uid, String name, String message) {
        this.uid = uid;
        this.name = name;
        this.message = message;
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

}
