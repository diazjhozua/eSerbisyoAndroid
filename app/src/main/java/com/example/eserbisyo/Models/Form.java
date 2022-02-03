package com.example.eserbisyo.Models;

import java.util.Date;

public class Form {

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
}
