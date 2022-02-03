package com.example.eserbisyo.Models;

public class Comment {
    private int id;
    private String body;
    private User user;
    private String createdAt;

    public Comment(int id,  String body, User user, String createdAt) {
        this.id = id;
        this.body = body;
        this.user = user;
        this.createdAt = createdAt;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
