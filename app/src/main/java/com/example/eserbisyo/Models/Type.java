package com.example.eserbisyo.Models;


public class Type {
    private int id;
    private String name;
    private String count;

    public Type(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Type() {
    }

    public Type(int id, String name, String count) {
        this.id = id;
        this.name = name;
        this.count = count;
    }




    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
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


}
