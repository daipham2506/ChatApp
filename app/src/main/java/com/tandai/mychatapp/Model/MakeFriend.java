package com.tandai.mychatapp.Model;

public class MakeFriend {
    private String sender;
    private String receiver;

    public MakeFriend(String sender, String receiver) {
        this.sender = sender;
        this.receiver = receiver;

    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public MakeFriend() {
    }
}
