package com.example.eserbisyo.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Certificate implements Parcelable {
    private int id;
    private String name;
    private double price;
    private String status;
    private String deliveryOption;
    private String requirementsCount;
    private ArrayList<Requirement> requirementArrayList;


    public Certificate(int id, String name, double price, String status, String deliveryOption, String requirementsCount, ArrayList<Requirement> requirementArrayList) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.status = status;
        this.deliveryOption = deliveryOption;
        this.requirementsCount = requirementsCount;
        this.requirementArrayList = requirementArrayList;
    }

    public Certificate(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeliveryOption() {
        return deliveryOption;
    }

    public void setDeliveryOption(String deliveryOption) {
        this.deliveryOption = deliveryOption;
    }

    public String getRequirementsCount() {
        return requirementsCount;
    }

    public void setRequirementsCount(String requirementsCount) {
        this.requirementsCount = requirementsCount;
    }

    public ArrayList<Requirement> getRequirementArrayList() {
        return requirementArrayList;
    }

    public void setRequirementArrayList(ArrayList<Requirement> requirementArrayList) {
        this.requirementArrayList = requirementArrayList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeDouble(this.price);
        dest.writeString(this.status);
        dest.writeString(this.deliveryOption);
        dest.writeString(this.requirementsCount);
        dest.writeList(this.requirementArrayList);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readInt();
        this.name = source.readString();
        this.price = source.readDouble();
        this.status = source.readString();
        this.deliveryOption = source.readString();
        this.requirementsCount = source.readString();
        this.requirementArrayList = new ArrayList<Requirement>();
        source.readList(this.requirementArrayList, Requirement.class.getClassLoader());
    }

    protected Certificate(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.price = in.readDouble();
        this.status = in.readString();
        this.deliveryOption = in.readString();
        this.requirementsCount = in.readString();
        this.requirementArrayList = new ArrayList<Requirement>();
        in.readList(this.requirementArrayList, Requirement.class.getClassLoader());
    }

    public static final Parcelable.Creator<Certificate> CREATOR = new Parcelable.Creator<Certificate>() {
        @Override
        public Certificate createFromParcel(Parcel source) {
            return new Certificate(source);
        }

        @Override
        public Certificate[] newArray(int size) {
            return new Certificate[size];
        }
    };
}
