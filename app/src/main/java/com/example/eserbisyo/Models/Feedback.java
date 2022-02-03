package com.example.eserbisyo.Models;

public class Feedback {
    private int id;
    private String submittedAs;
    private Type type;
    private String customType;
    private String polarity;
    private String message;
    private String adminRespond;
    private String status;
    private String createdAt;
    private String respondedAt;

    public Feedback() {
    }

    public Feedback(int id, String submittedAs, Type type, String customType, String polarity, String message, String adminRespond, String status, String createdAt, String respondedAt) {
        this.id = id;
        this.submittedAs = submittedAs;
        this.type = type;
        this.customType = customType;
        this.polarity = polarity;
        this.submittedAs = submittedAs;
        this.message = message;
        this.submittedAs = submittedAs;
        this.adminRespond = adminRespond;
        this.submittedAs = submittedAs;
        this.status = status;
        this.submittedAs = submittedAs;
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

    public String getPolarity(){
        return polarity;
    }

    public void setPolarity(String polarity) {
        this.polarity = polarity;
    }

    public String getMessage(){
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAdminRespond(){
        return adminRespond;
    }

    public void setAdminRespond(String adminRespond) {
        this.adminRespond = adminRespond;
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

    public void setRespondedAt(String respondedAt) {
        this.respondedAt = respondedAt;
    }
}
