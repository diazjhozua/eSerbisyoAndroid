package com.example.eserbisyo.Models;

public class Report {
    private int id;
    private String submittedAs;
    private Type type;
    private String customType;
    private String locationAddress;
    private String landmark;
    private String description;

    private String urgentClassification;
    private String pictureName;
    private String filePath;
    private String adminMessage;
    private String status;
    private String createdAt;
    private String respondedAt;

    public Report() {
    }

    public Report(int id, String submittedAs, Type type, String customType, String locationAddress, String landmark, String description, String urgentClassification,
                  String pictureName, String filePath, String adminMessage,String status, String createdAt, String respondedAt ) {
        this.id = id;
        this.submittedAs = submittedAs;
        this.type = type;
        this.customType = customType;
        this.locationAddress = locationAddress;
        this.landmark = landmark;
        this.description = description;
        this.submittedAs = submittedAs;
        this.urgentClassification = urgentClassification;
        this.pictureName = pictureName;
        this.filePath = filePath;
        this.adminMessage = adminMessage;
        this.status = status;
        this.createdAt = createdAt;
        this.respondedAt = respondedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubmittedAs(){
        return submittedAs;
    }

    public void setSubmittedAs(String submittedAs) {
        this.submittedAs = submittedAs;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getCustomType(){
        return customType;
    }

    public void setCustomType(String customType) {
        this.customType = customType;
    }

    public String getLocationAddress(){
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public String getLandmark(){
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrgentClassification(){
        return urgentClassification;
    }

    public void setUrgentClassification(String urgentClassification) {
        this.urgentClassification = urgentClassification;
    }

    public String getPictureName(){
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    public String getFilePath(){
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getAdminMessage(){
        return adminMessage;
    }

    public void setAdminMessage(String adminMessage) {
        this.adminMessage = adminMessage;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt(){
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getRespondedAt(){
        return respondedAt;
    }

    public void getRespondedAt(String respondedAt) {
        this.respondedAt = respondedAt;
    }

}
