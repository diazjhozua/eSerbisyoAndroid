package com.example.eserbisyo.Models;

public class User {
    private int id;
    private String name;
    private String phoneNo;
    private String email;
    private String pictureName;
    private String filePath;

    private String bikeType;
    private String bikeColor;
    private String bikeSize;

    public User(int id, String name, String pictureName, String filePath) {
        this.id = id;
        this.name = name;
        this.pictureName = pictureName;
        this.filePath = filePath;
    }
    // for biker constructor
    public User(int id, String name, String phoneNo, String email, String pictureName, String filePath, String bikeType, String bikeColor, String bikeSize) {
        this.id = id;
        this.name = name;
        this.phoneNo = phoneNo;
        this.email = email;
        this.pictureName = pictureName;
        this.filePath = filePath;
        this.bikeType = bikeType;
        this.bikeColor = bikeColor;
        this.bikeSize = bikeSize;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBikeType() {
        return bikeType;
    }

    public void setBikeType(String bikeType) {
        this.bikeType = bikeType;
    }

    public String getBikeColor() {
        return bikeColor;
    }

    public void setBikeColor(String bikeColor) {
        this.bikeColor = bikeColor;
    }

    public String getBikeSize() {
        return bikeSize;
    }

    public void setBikeSize(String bikeSize) {
        this.bikeSize = bikeSize;
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
