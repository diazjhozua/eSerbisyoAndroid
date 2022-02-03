package com.example.eserbisyo.Models;

public class Ordinance {
    private int id;
    private Type type;
    private String customType;
    private String ordinanceNo;
    private String title;
    private String dateApproved;
    private String fileName;
    private String filePath;
    private String createdAt;

    public Ordinance(int id, Type type, String customType, String ordinanceNo,
                     String title, String dateApproved, String fileName,
                     String filePath, String createdAt) {
        this.id = id;
        this.type = type;
        this.customType = customType;
        this.ordinanceNo = ordinanceNo;
        this.title = title;
        this.dateApproved = dateApproved;
        this.fileName = fileName;
        this.filePath = filePath;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getOrdinanceNo() {
        return ordinanceNo;
    }

    public void setOrdinanceNo(String ordinanceNo) {
        this.ordinanceNo = ordinanceNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateApproved() {
        return dateApproved;
    }

    public void setDateApproved(String dateApproved) {
        this.dateApproved = dateApproved;
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
}
