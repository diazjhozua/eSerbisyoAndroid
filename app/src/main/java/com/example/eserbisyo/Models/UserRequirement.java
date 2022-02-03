package com.example.eserbisyo.Models;

public class UserRequirement {

    private int id;
    private int userId;
    private int requirementId;
    private String fileName;
    private String filePath;
    private String createdAt;
    private Requirement requirement;

    public UserRequirement(int id, int userId, int requirementId, String fileName, String filePath, String createdAt, Requirement requirement) {
        this.id = id;
        this.userId = userId;
        this.requirementId = requirementId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.createdAt = createdAt;
        this.requirement = requirement;
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

    public int getRequirementId() {
        return requirementId;
    }

    public void setRequirementId(int requirementId) {
        this.requirementId = requirementId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public void setRequirement(Requirement requirement) {
        this.requirement = requirement;
    }


}
