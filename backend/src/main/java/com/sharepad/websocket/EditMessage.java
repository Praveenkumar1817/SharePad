package com.sharepad.websocket;

public class EditMessage {
    private String senderEmail;
    private String content;

    public EditMessage() {
    }

    public EditMessage(String senderEmail, String content) {
        this.senderEmail = senderEmail;
        this.content = content;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
