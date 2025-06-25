package com.example.sharetrip.models;

public class Chat {
    private Integer user_id;
    private String username;
    private String content;
    private String createdAt;

    public Chat(Integer user_id, String username, String content, String createdAt) {
        this.user_id = user_id;
        this.username = username;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
