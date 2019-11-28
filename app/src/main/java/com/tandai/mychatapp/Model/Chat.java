package com.tandai.mychatapp.Model;

public class Chat {
    private int type;
    private String sender;
    private String receiver;
    private String message;
    private String linkFile;
    private String time;
    private boolean isSeen;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getTime() {
        return time;
    }

    public String getLinkFile() {
        return linkFile;
    }

    public void setLinkFile(String linkFile) {
        this.linkFile = linkFile;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Chat(int type, String sender, String receiver, String message, String linkFile, String time, boolean isSeen) {
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.linkFile = linkFile;
        this.time = time;
        this.isSeen = isSeen;
    }

    public boolean getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(boolean seen) {
        isSeen = seen;
    }

    public Chat() {
    }
}
