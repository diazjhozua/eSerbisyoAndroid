package com.example.eserbisyo.Models;

public class Notification {
    private int id;
    private String message;
    private String seenStatus;
    private String modelType;
    private int modelId;
    private String createdAt;

    public Notification(int id, String message, String seenStatus, String modelType, int modelId, String createdAt) {
        this.id = id;
        this.message = message;
        this.seenStatus = seenStatus;
        this.modelType = modelType;
        this.modelId = modelId;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getSeenStatus() {
        return seenStatus;
    }

    public String getModelType() {
        return modelType;
    }

    public int getModelId() {
        return modelId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setSeenStatus(String seenStatus) {
        this.seenStatus = seenStatus;
    }
}
