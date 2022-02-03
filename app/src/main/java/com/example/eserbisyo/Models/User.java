package com.example.eserbisyo.Models;

public class User {
    private int id;
    private String name;
    private String pictureName;
    private String filePath;

    public User(int id, String name, String pictureName, String filePath) {
        this.id = id;
        this.name = name;
        this.pictureName = pictureName;
        this.filePath = filePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
