package com.example.quizme;

import java.util.Date;

public class WithdrawRequest {
    private int withdrawId;
    private int userId;
    private String emailAddress;
    private String requestedBy;
    private Date createdAt;

    public WithdrawRequest() {
    }

    public WithdrawRequest(int userId, String emailAddress, String requestedBy) {
        this.userId = userId;
        this.emailAddress = emailAddress;
        this.requestedBy = requestedBy;
        this.createdAt = new Date();
    }

    public int getWithdrawId() {
        return withdrawId;
    }

    public void setWithdrawId(int withdrawId) {
        this.withdrawId = withdrawId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
