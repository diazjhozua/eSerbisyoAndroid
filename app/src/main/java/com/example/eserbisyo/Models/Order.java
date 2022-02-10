package com.example.eserbisyo.Models;

public class Order {
    private int id;
    private String name;
    private String email;
    private String phoneNo;
    private String locationAddress;
    private String createdAt;
    private String orderType;
    private String orderStatus;
    private String pickupAt;
    private String receivedAt;
    private Double totalPrice;
    private Double deliveryFee;
    private String applicationStatus;
    private String paymentStatus;
    private Boolean isBooked;
    private String returnedStatus;
    private User mBiker;
    private String adminMessage;
    private String updatedAt;



    /*Constructor for biker transaction and available order fragments*/
    public Order(int id, String createdAt, String orderStatus, String pickupAt, String receivedAt, Double totalPrice, Double deliveryFee, String paymentStatus, Boolean isBooked, String returnedStatus) {
        this.id = id;
        this.createdAt = createdAt;
        this.orderStatus = orderStatus;
        this.pickupAt = pickupAt;
        this.receivedAt = receivedAt;
        this.totalPrice = totalPrice;
        this.deliveryFee = deliveryFee;
        this.paymentStatus = paymentStatus;
        this.isBooked = isBooked;
        this.returnedStatus = returnedStatus;
    }

    /*Constructor for biker order activity */
    public Order(int id, String name, String email, String phoneNo, String locationAddress, String orderType, String orderStatus, String pickupAt, String receivedAt, Double totalPrice, Double deliveryFee, String paymentStatus, Boolean isBooked, String returnedStatus) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.locationAddress = locationAddress;
        this.orderType = orderType;
        this.orderStatus = orderStatus;
        this.pickupAt = pickupAt;
        this.receivedAt = receivedAt;
        this.totalPrice = totalPrice;
        this.deliveryFee = deliveryFee;
        this.paymentStatus = paymentStatus;
        this.isBooked = isBooked;
        this.returnedStatus = returnedStatus;
    }


    public Order(int id, String createdAt, String orderType, String orderStatus, String pickupAt, String receivedAt, Double totalPrice, Double deliveryFee, String applicationStatus, User mBiker, String adminMessage, String updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.orderType = orderType;
        this.orderStatus = orderStatus;
        this.pickupAt = pickupAt;
        this.receivedAt = receivedAt;
        this.totalPrice = totalPrice;
        this.deliveryFee = deliveryFee;
        this.applicationStatus = applicationStatus;
        this.mBiker = mBiker;
        this.adminMessage = adminMessage;
        this.updatedAt = updatedAt;
    }


    public Order(int id, String name, String email, String phoneNo, String locationAddress, String createdAt, String orderType, String orderStatus, String pickupAt, String receivedAt, Double totalPrice, Double deliveryFee, String applicationStatus, User mBiker, String adminMessage, String updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.locationAddress = locationAddress;
        this.createdAt = createdAt;
        this.orderType = orderType;
        this.orderStatus = orderStatus;
        this.pickupAt = pickupAt;
        this.receivedAt = receivedAt;
        this.totalPrice = totalPrice;
        this.deliveryFee = deliveryFee;
        this.applicationStatus = applicationStatus;
        this.mBiker = mBiker;
        this.adminMessage = adminMessage;
        this.updatedAt = updatedAt;
    }

//    public Order(int id, String name, String email, String phoneNo, String locationAddress, String createdAt, String orderType, String orderStatus, String receivedAt, Double totalPrice, Double deliveryFee, String applicationStatus, User mBiker, String adminMessage, String updatedAt) {
//        this.id = id;
//        this.name = name;
//        this.email = email;
//        this.phoneNo = phoneNo;
//        this.locationAddress = locationAddress;
//        this.createdAt = createdAt;
//        this.orderType = orderType;
//        this.orderStatus = orderStatus;
//        this.receivedAt = receivedAt;
//        this.totalPrice = totalPrice;
//        this.deliveryFee = deliveryFee;
//        this.applicationStatus = applicationStatus;
//        this.mBiker = mBiker;
//        this.adminMessage = adminMessage;
//        this.updatedAt = updatedAt;
//    }


    public String getReturnedStatus() {
        return returnedStatus;
    }

    public void setReturnedStatus(String returnedStatus) {
        this.returnedStatus = returnedStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Boolean getBooked() {
        return isBooked;
    }

    public void setBooked(Boolean booked) {
        isBooked = booked;
    }

    public String getPickupAt() {
        return pickupAt;
    }

    public void setPickupAt(String pickupAt) {
        this.pickupAt = pickupAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getmBiker() {
        return mBiker;
    }

    public void setmBiker(User mBiker) {
        this.mBiker = mBiker;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(String receivedAt) {
        this.receivedAt = receivedAt;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(Double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(String applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public String getAdminMessage() {
        return adminMessage;
    }

    public void setAdminMessage(String adminMessage) {
        this.adminMessage = adminMessage;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
