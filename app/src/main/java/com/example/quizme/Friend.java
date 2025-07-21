package com.example.quizme;

import java.util.Date;

public class Friend {
    private int friendshipId;
    private int user1Id;
    private int user2Id;
    private Date friendshipDate;
    
    // Thông tin bạn bè (người còn lại)
    private String friendName;
    private String friendEmail;
    private String friendAvatar;
    private long friendCoins;

    public Friend() {}

    public Friend(int user1Id, int user2Id) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.friendshipDate = new Date();
    }

    // Getters and Setters
    public int getFriendshipId() { return friendshipId; }
    public void setFriendshipId(int friendshipId) { this.friendshipId = friendshipId; }

    public int getUser1Id() { return user1Id; }
    public void setUser1Id(int user1Id) { this.user1Id = user1Id; }

    public int getUser2Id() { return user2Id; }
    public void setUser2Id(int user2Id) { this.user2Id = user2Id; }

    public Date getFriendshipDate() { return friendshipDate; }
    public void setFriendshipDate(Date friendshipDate) { this.friendshipDate = friendshipDate; }

    public String getFriendName() { return friendName; }
    public void setFriendName(String friendName) { this.friendName = friendName; }

    public String getFriendEmail() { return friendEmail; }
    public void setFriendEmail(String friendEmail) { this.friendEmail = friendEmail; }

    public String getFriendAvatar() { return friendAvatar; }
    public void setFriendAvatar(String friendAvatar) { this.friendAvatar = friendAvatar; }

    public long getFriendCoins() { return friendCoins; }
    public void setFriendCoins(long friendCoins) { this.friendCoins = friendCoins; }
}
