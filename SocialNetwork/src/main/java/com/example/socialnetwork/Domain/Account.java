package com.example.socialnetwork.Domain;

public class Account extends Entity<String> {
    private String username;
    private String password;

    private Long userId;


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Account(String username, String password, Long userID) {
        this.username = username;
        this.password = password;
        this.userId = userID;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "username=" + username +
                "password=" + password +
                "userId=" + userId +
                '}';
    }
}
