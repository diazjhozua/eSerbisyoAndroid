package com.example.eserbisyo.Models;

import android.graphics.Bitmap;

public class Complainant {
    private int id;
    private int complaintId;
    private boolean isModifiable;
    private boolean isCreating;
    private String name;
    private Bitmap bitmapSignature;
    private String fileName;
    private String filePath;


    public Complainant(int id, int complaintId, boolean isModifiable, boolean isCreating, String name, String fileName, String filePath) {
        this.id = id;
        this.complaintId = complaintId;
        this.isModifiable = isModifiable;
        this.isCreating = isCreating;
        this.name = name;
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public Complainant(boolean isModifiable, boolean isCreating, String name, Bitmap bitmapSignature) {
        this.isModifiable = isModifiable;
        this.isCreating = isCreating;
        this.name = name;
        this.bitmapSignature = bitmapSignature;
    }

    public Bitmap getBitmapSignature() {
        return bitmapSignature;
    }

    public void setBitmapSignature(Bitmap bitmapSignature) {
        this.bitmapSignature = bitmapSignature;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(int complaintId) {
        this.complaintId = complaintId;
    }

    public boolean isModifiable() {
        return isModifiable;
    }

    public void setModifiable(boolean modifiable) {
        isModifiable = modifiable;
    }

    public boolean isCreating() {
        return isCreating;
    }

    public void setCreating(boolean creating) {
        isCreating = creating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
