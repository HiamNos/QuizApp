package com.example.quizme;

import java.util.Date;

public class Message {
    private int messageId;
    private int senderUserId;
    private int receiverUserId;
    private String messageContent;
    private Date messageDate;
    private boolean isRead;
    
    // Thông tin bổ sung
    private String senderName;
    private String senderAvatar;
    private String receiverName;
    private String receiverAvatar;

    public Message() {}

    public Message(int senderUserId, int receiverUserId, String messageContent) {
        this.senderUserId = senderUserId;
        this.receiverUserId = receiverUserId;
        this.messageContent = messageContent;
        this.messageDate = new Date();
        this.isRead = false;
    }

    // Getters and Setters
    public int getMessageId() { return messageId; }
    public void setMessageId(int messageId) { this.messageId = messageId; }

    public int getSenderUserId() { return senderUserId; }
    public void setSenderUserId(int senderUserId) { this.senderUserId = senderUserId; }

    public int getReceiverUserId() { return receiverUserId; }
    public void setReceiverUserId(int receiverUserId) { this.receiverUserId = receiverUserId; }

    public String getMessageContent() { return messageContent; }
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }

    public Date getMessageDate() { return messageDate; }
    public void setMessageDate(Date messageDate) { this.messageDate = messageDate; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderAvatar() { return senderAvatar; }
    public void setSenderAvatar(String senderAvatar) { this.senderAvatar = senderAvatar; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getReceiverAvatar() { return receiverAvatar; }
    public void setReceiverAvatar(String receiverAvatar) { this.receiverAvatar = receiverAvatar; }
}
