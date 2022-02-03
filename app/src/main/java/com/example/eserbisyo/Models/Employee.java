package com.example.eserbisyo.Models;

public class Employee {
    private int id;
    private String name;
    private int termId;
    private String term;
    private String customTerm;
    private int posId;
    private String position;
    private String customPosition;
    private String description;
    private String fileName;
    private String filePath;

    public Employee(int id, String name, int termId, String term, String customTerm, int posId, String position, String customPosition, String description, String fileName, String filePath) {
        this.id = id;
        this.name = name;
        this.termId = termId;
        this.term = term;
        this.customTerm = customTerm;
        this.posId = posId;
        this.position = position;
        this.customPosition = customPosition;
        this.description = description;
        this.fileName = fileName;
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

    public int getTermId() {
        return termId;
    }

    public void setTermId(int termId) {
        this.termId = termId;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getCustomTerm() {
        return customTerm;
    }

    public void setCustomTerm(String customTerm) {
        this.customTerm = customTerm;
    }

    public int getPosId() {
        return posId;
    }

    public void setPosId(int posId) {
        this.posId = posId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCustomPosition() {
        return customPosition;
    }

    public void setCustomPosition(String customPosition) {
        this.customPosition = customPosition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
