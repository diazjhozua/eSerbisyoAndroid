package com.example.eserbisyo.Models;

public class Complaint {
    private int id;
    private int userId;
    private String userName;
    private Type type;
    private String customType;
    private String reason;
    private String action;
    private String email;
    private String phoneNo;
    private String status;
    private String adminMessage;
    private String createdAt;
    private String updatedAt;

    public Complaint(int id, int userId, String userName, Type type, String customType, String reason, String action, String email, String phoneNo, String status, String adminMessage, String createdAt, String updatedAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.type = type;
        this.customType = customType;
        this.reason = reason;
        this.action = action;
        this.email = email;
        this.phoneNo = phoneNo;
        this.status = status;
        this.adminMessage = adminMessage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getCustomType() {
        return customType;
    }

    public void setCustomType(String customType) {
        this.customType = customType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdminMessage() {
        return adminMessage;
    }

    public void setAdminMessage(String adminMessage) {
        this.adminMessage = adminMessage;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
