package com.braumsolutions.advogadoresponde.Model;

public class ChatMessage {

    private String sender;
    private String receiver;
    private String name;
    private String message;

    public ChatMessage() {

    }

    public ChatMessage(String uid, String name, String message) {
        this.sender = uid;
        this.name = name;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
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

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
