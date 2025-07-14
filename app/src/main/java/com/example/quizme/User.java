package com.example.quizme;

public class User {
    private int userId;
    private String name, email, password, profile, referCode, role;
    private long coins = 25;

    public User() {
    }

    public User(String name, String email, String password, String referCode) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.referCode = referCode;
        this.role = "user"; // Mặc định là user
    }

    public User(String name, String email, String password, String referCode, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.referCode = referCode;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getReferCode() {
        return referCode;
    }

    public void setReferCode(String referCode) {
        this.referCode = referCode;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public long getCoins() {
        return coins;
    }

    public void setCoins(long coins) {
        this.coins = coins;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    // Phương thức kiểm tra xem user có phải admin không
    public boolean isAdmin() {
        return "admin".equals(role);
    }
}
