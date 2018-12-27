package com.braumsolutions.advogadoresponde.Model;

public class Message {

        private String sender;
        private String receiver;
        private String message;
        private String key;

        public Message() {
        }

    public Message(String sender, String recevier, String message, String key) {
        this.sender = sender;
        this.receiver = recevier;
        this.message = message;
        this.key = key;
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
