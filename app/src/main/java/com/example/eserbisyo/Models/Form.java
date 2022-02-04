package com.example.eserbisyo.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Form implements Parcelable {

    private int id;
    private int certId;
    private String certName;
    private Double certPrice;
    private String firstName;
    private String middleName;
    private String lastName;
    private String address;
    private String civilStatus;
    private String birthday;
    private String citizenship;

    private String purpose;

    private String businessName;

    private String birthplace;

    private Double height;
    private Double weight;
    private String profession;
    private String cedulaType;
    private String sex;
    private String tinNo;
    private String icrNo;

    private String phoneNo;
    private String contactPerson;
    private String contactPersonPhoneNo;
    private String contactPersonRelation;

    public Form(int id, int certId, String certName, Double certPrice) {
        this.id = id;
        this.certId = certId;
        this.certName = certName;
        this.certPrice = certPrice;
    }



    public Form(int id, int certId, String certName, Double certPrice, String firstName, String middleName, String lastName, String address, String civilStatus, String birthday, String citizenship, String purpose, String businessName, String birthplace, Double height, Double weight, String profession, String cedulaType, String sex, String tinNo, String icrNo, String phoneNo, String contactPerson, String contactPersonPhoneNo, String contactPersonRelation) {
        this.id = id;
        this.certId = certId;
        this.certName = certName;
        this.certPrice = certPrice;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.address = address;
        this.civilStatus = civilStatus;
        this.birthday = birthday;
        this.citizenship = citizenship;
        this.purpose = purpose;
        this.businessName = businessName;
        this.birthplace = birthplace;
        this.height = height;
        this.weight = weight;
        this.profession = profession;
        this.cedulaType = cedulaType;
        this.sex = sex;
        this.tinNo = tinNo;
        this.icrNo = icrNo;
        this.phoneNo = phoneNo;
        this.contactPerson = contactPerson;
        this.contactPersonPhoneNo = contactPersonPhoneNo;
        this.contactPersonRelation = contactPersonRelation;
    }

/*    // for indigency and clearance
    public Form(int id, int certId, String certName, Double certPrice, String firstName, String middleName, String lastName, String address, String civilStatus, String birthday, String citizenship, String purpose) {
        this.id = id;
        this.certId = certId;
        this.certName = certName;
        this.certPrice = certPrice;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.address = address;
        this.civilStatus = civilStatus;
        this.birthday = birthday;
        this.citizenship = citizenship;
        this.purpose = purpose;
    }

    // for cedula
    public Form(int id, int certId, String certName, Double certPrice, String firstName, String middleName, String lastName, String address, String civilStatus, String birthday, String citizenship, String birthplace, Double height, Double weight, String profession, String cedulaType, String sex, int tinNo, int icrNo) {
        this.id = id;
        this.certId = certId;
        this.certName = certName;
        this.certPrice = certPrice;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.address = address;
        this.civilStatus = civilStatus;
        this.birthday = birthday;
        this.citizenship = citizenship;
        this.birthplace = birthplace;
        this.height = height;
        this.weight = weight;
        this.profession = profession;
        this.cedulaType = cedulaType;
        this.sex = sex;
        this.tinNo = tinNo;
        this.icrNo = icrNo;
    }

    // for id
    public Form(int id, int certId, String certName, Double certPrice, String firstName, String middleName, String lastName, String address, String civilStatus, String birthday, String citizenship, String birthplace, String phoneNo, String contactPerson, String contactPersonPhoneNo, String contactPersonRelation) {
        this.id = id;
        this.certId = certId;
        this.certName = certName;
        this.certPrice = certPrice;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.address = address;
        this.civilStatus = civilStatus;
        this.birthday = birthday;
        this.citizenship = citizenship;
        this.birthplace = birthplace;
        this.phoneNo = phoneNo;
        this.contactPerson = contactPerson;
        this.contactPersonPhoneNo = contactPersonPhoneNo;
        this.contactPersonRelation = contactPersonRelation;
    }

    // for business
    public Form(int id, int certId, String certName, Double certPrice, String firstName, String middleName, String lastName, String address, String businessName) {
        this.id = id;
        this.certId = certId;
        this.certName = certName;
        this.certPrice = certPrice;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.address = address;
        this.businessName = businessName;
    }*/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCertId() {
        return certId;
    }

    public void setCertId(int certId) {
        this.certId = certId;
    }

    public String getCertName() {
        return certName;
    }

    public void setCertName(String certName) {
        this.certName = certName;
    }

    public Double getCertPrice() {
        return certPrice;
    }

    public void setCertPrice(Double certPrice) {
        this.certPrice = certPrice;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCivilStatus() {
        return civilStatus;
    }

    public void setCivilStatus(String civilStatus) {
        this.civilStatus = civilStatus;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public void setBirthplace(String birthplace) {
        this.birthplace = birthplace;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getCedulaType() {
        return cedulaType;
    }

    public void setCedulaType(String cedulaType) {
        this.cedulaType = cedulaType;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getTinNo() {
        return tinNo;
    }

    public void setTinNo(String tinNo) {
        this.tinNo = tinNo;
    }

    public String getIcrNo() {
        return icrNo;
    }

    public void setIcrNo(String icrNo) {
        this.icrNo = icrNo;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPersonPhoneNo() {
        return contactPersonPhoneNo;
    }

    public void setContactPersonPhoneNo(String contactPersonPhoneNo) {
        this.contactPersonPhoneNo = contactPersonPhoneNo;
    }

    public String getContactPersonRelation() {
        return contactPersonRelation;
    }

    public void setContactPersonRelation(String contactPersonRelation) {
        this.contactPersonRelation = contactPersonRelation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.certId);
        dest.writeString(this.certName);
        dest.writeValue(this.certPrice);
        dest.writeString(this.firstName);
        dest.writeString(this.middleName);
        dest.writeString(this.lastName);
        dest.writeString(this.address);
        dest.writeString(this.civilStatus);
        dest.writeString(this.birthday);
        dest.writeString(this.citizenship);
        dest.writeString(this.purpose);
        dest.writeString(this.businessName);
        dest.writeString(this.birthplace);
        dest.writeValue(this.height);
        dest.writeValue(this.weight);
        dest.writeString(this.profession);
        dest.writeString(this.cedulaType);
        dest.writeString(this.sex);
        dest.writeString(this.tinNo);
        dest.writeString(this.icrNo);
        dest.writeString(this.phoneNo);
        dest.writeString(this.contactPerson);
        dest.writeString(this.contactPersonPhoneNo);
        dest.writeString(this.contactPersonRelation);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readInt();
        this.certId = source.readInt();
        this.certName = source.readString();
        this.certPrice = (Double) source.readValue(Double.class.getClassLoader());
        this.firstName = source.readString();
        this.middleName = source.readString();
        this.lastName = source.readString();
        this.address = source.readString();
        this.civilStatus = source.readString();
        this.birthday = source.readString();
        this.citizenship = source.readString();
        this.purpose = source.readString();
        this.businessName = source.readString();
        this.birthplace = source.readString();
        this.height = (Double) source.readValue(Double.class.getClassLoader());
        this.weight = (Double) source.readValue(Double.class.getClassLoader());
        this.profession = source.readString();
        this.cedulaType = source.readString();
        this.sex = source.readString();
        this.tinNo = source.readString();
        this.icrNo = source.readString();
        this.phoneNo = source.readString();
        this.contactPerson = source.readString();
        this.contactPersonPhoneNo = source.readString();
        this.contactPersonRelation = source.readString();
    }

    protected Form(Parcel in) {
        this.id = in.readInt();
        this.certId = in.readInt();
        this.certName = in.readString();
        this.certPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.firstName = in.readString();
        this.middleName = in.readString();
        this.lastName = in.readString();
        this.address = in.readString();
        this.civilStatus = in.readString();
        this.birthday = in.readString();
        this.citizenship = in.readString();
        this.purpose = in.readString();
        this.businessName = in.readString();
        this.birthplace = in.readString();
        this.height = (Double) in.readValue(Double.class.getClassLoader());
        this.weight = (Double) in.readValue(Double.class.getClassLoader());
        this.profession = in.readString();
        this.cedulaType = in.readString();
        this.sex = in.readString();
        this.tinNo = in.readString();
        this.icrNo = in.readString();
        this.phoneNo = in.readString();
        this.contactPerson = in.readString();
        this.contactPersonPhoneNo = in.readString();
        this.contactPersonRelation = in.readString();
    }

    public static final Parcelable.Creator<Form> CREATOR = new Parcelable.Creator<Form>() {
        @Override
        public Form createFromParcel(Parcel source) {
            return new Form(source);
        }

        @Override
        public Form[] newArray(int size) {
            return new Form[size];
        }
    };
}
