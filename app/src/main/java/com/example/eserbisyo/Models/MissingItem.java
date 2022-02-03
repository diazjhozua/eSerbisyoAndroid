package com.example.eserbisyo.Models;

import java.util.ArrayList;

public class MissingItem {
    private int id;
    private int userId;
    private String userName;
    private String userPictureName;
    private String userPicturePath;
    private String reportType;
    private String itemName;
    private String lastSeen;
    private String description;
    private String email;
    private String phoneNo;
    private String pictureName;
    private String picturePath;
    private String credentialName;
    private String credentialPath;
    private int commentsCount;
    private ArrayList<Comment> commentArray;
    private String status;
    private String adminMessage;
    private String createdAt;
    private String updatedAt;


    public MissingItem(int id, int userId, String userName, String userPictureName, String userPicturePath, String reportType, String itemName, String lastSeen, String description, String email, String phoneNo, String pictureName, String picturePath, String credentialName, String credentialPath, int commentsCount, String status, String adminMessage, String createdAt, String updatedAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.userPictureName = userPictureName;
        this.userPicturePath = userPicturePath;
        this.reportType = reportType;
        this.itemName = itemName;
        this.lastSeen = lastSeen;
        this.description = description;
        this.email = email;
        this.phoneNo = phoneNo;
        this.pictureName = pictureName;
        this.picturePath = picturePath;
        this.credentialName = credentialName;
        this.credentialPath = credentialPath;
        this.commentsCount = commentsCount;
        this.status = status;
        this.adminMessage = adminMessage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUserPictureName() {
        return userPictureName;
    }

    public void setUserPictureName(String userPictureName) {
        this.userPictureName = userPictureName;
    }

    public String getUserPicturePath() {
        return userPicturePath;
    }

    public void setUserPicturePath(String userPicturePath) {
        this.userPicturePath = userPicturePath;
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

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getCredentialName() {
        return credentialName;
    }

    public void setCredentialName(String credentialName) {
        this.credentialName = credentialName;
    }

    public String getCredentialPath() {
        return credentialPath;
    }

    public void setCredentialPath(String credentialPath) {
        this.credentialPath = credentialPath;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public ArrayList<Comment> getCommentArray() {
        return commentArray;
    }

    public void setCommentArray(ArrayList<Comment> commentArray) {
        this.commentArray = commentArray;
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
}
