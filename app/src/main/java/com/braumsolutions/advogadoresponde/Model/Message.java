package com.braumsolutions.advogadoresponde.Model;

public class Message {

    private String sender;
    private String receiver;
    private String message;
    private String key;
    private String send_date;

    public Message() {
    }

    public Message(String sender, String recevier, String message, String key, String sendDate) {
        this.sender = sender;
        this.receiver = recevier;
        this.message = message;
        this.key = key;
        this.send_date = sendDate;
    }

    public String getSend_date() {
        return send_date;
    }

    public void setSend_date(String send_date) {
        this.send_date = send_date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
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

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }


}
