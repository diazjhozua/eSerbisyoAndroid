package com.example.eserbisyo.Models;

public class Project {
    private int id;
    private Type type;
    private String customType;
    private String name;
    private double cost;
    private String description;
    private String projectStart;
    private String projectEnd;
    private String location;
    private String fileName;
    private String filePath;
    private String createdAt;

    public Project(int id, Type type, String customType, String name,
                   double cost, String description, String projectStart,
                   String projectEnd, String location, String fileName,
                   String filePath, String createdAt) {
        this.id = id;
        this.type = type;
        this.customType = customType;
        this.name = name;
        this.cost = cost;
        this.description = description;
        this.projectStart = projectStart;
        this.projectEnd = projectEnd;
        this.location = location;
        this.fileName = fileName;
        this.filePath = filePath;
        this.createdAt = createdAt;
    }



    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getProjectStart() {
        return projectStart;
    }

    public void setProjectStart(String projectStart) {
        this.projectStart = projectStart;
    }

    public String getProjectEnd() {
        return projectEnd;
    }

    public void setProjectEnd(String projectEnd) {
        this.projectEnd = projectEnd;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
