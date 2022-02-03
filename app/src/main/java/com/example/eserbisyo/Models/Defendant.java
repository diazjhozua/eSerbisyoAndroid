package com.example.eserbisyo.Models;

public class Defendant {
    private int id;
    private int complaintId;
    private boolean isModifiable;
    private boolean isCreating;
    private String name;

    public Defendant(int id, int complaintId, boolean isModifiable, boolean isCreating, String name) {
        this.id = id;
        this.complaintId = complaintId;
        this.isModifiable = isModifiable;
        /*is from creating mode */
        this.isCreating = isCreating;
        this.name = name;
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
}
