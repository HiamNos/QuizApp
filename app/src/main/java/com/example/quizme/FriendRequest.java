package com.example.quizme;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FriendRequest {
    private int requestId;
    private int senderId;
    private int receiverId;
    private String status; // pending, accepted, rejected
    private Date requestDate;
    private Date responseDate;
    
    // Thông tin bổ sung từ bảng users
    private String senderName;
    private String senderEmail;
    private String senderAvatar;
    private String receiverName;
    private String receiverEmail;
    private String receiverAvatar;

    public FriendRequest() {}

    public FriendRequest(int senderId, int receiverId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = "pending";
        this.requestDate = new Date();
    }

    // Getters and Setters
    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public int getReceiverId() { return receiverId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getRequestDate() { return requestDate; }
    public void setRequestDate(Date requestDate) { this.requestDate = requestDate; }
    
    public String getRequestDateString() {
        if (requestDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return sdf.format(requestDate);
        }
        return "";
    }

    public Date getResponseDate() { return responseDate; }
    public void setResponseDate(Date responseDate) { this.responseDate = responseDate; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderEmail() { return senderEmail; }
    public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }

    public String getSenderAvatar() { return senderAvatar; }
    public void setSenderAvatar(String senderAvatar) { this.senderAvatar = senderAvatar; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getReceiverEmail() { return receiverEmail; }
    public void setReceiverEmail(String receiverEmail) { this.receiverEmail = receiverEmail; }

    public String getReceiverAvatar() { return receiverAvatar; }
    public void setReceiverAvatar(String receiverAvatar) { this.receiverAvatar = receiverAvatar; }
}
