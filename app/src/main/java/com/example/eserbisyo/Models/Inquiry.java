package com.example.eserbisyo.Models;

public class Inquiry {
    private int id;
    private String submittedAs;
    private String about;
    private String message;
    private String adminRespond;
    private String status;
    private String createdAt;
    private String respondedAt;

    public Inquiry(int id, String about, String message, String adminRespond, String status, String createdAt, String respondedAt) {
        this.id = id;
        this.about = about;
        this.message = message;
        this.adminRespond = adminRespond;
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

    public String getSubmittedAs() {
        return submittedAs;
    }

    public void setSubmittedAs(String submittedAs) {
        this.submittedAs = submittedAs;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAdminRespond() {
        return adminRespond;
    }

    public void setAdminRespond(String adminRespond) {
        this.adminRespond = adminRespond;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(String respondedAt) {
        this.respondedAt = respondedAt;
    }
}
