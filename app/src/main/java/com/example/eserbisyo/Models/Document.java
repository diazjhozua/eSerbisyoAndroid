package com.example.eserbisyo.Models;

public class Document {
    private int id;
    private Type type;
    private String customType;
    private String description;
    private String year;
    private String fileName;
    private String filePath;
    private String createdAt;

    public Document(int id, Type type, String customType, String description,
                    String year, String fileName, String filePath, String createdAt) {
        this.id = id;
        this.type = type;
        this.customType = customType;
        this.description = description;
        this.year = year;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
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
