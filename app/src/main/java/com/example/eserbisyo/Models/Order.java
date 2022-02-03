package com.example.eserbisyo.Models;

public class Order {
    private int id;
    private String createdAt;
    private String orderType;
    private String orderStatus;
    private String receivedAt;
    private Double totalPrice;
    private Double deliveryFee;
    private String applicationStatus;
    private String adminMessage;
    private String updatedAt;

    public Order(int id, String createdAt, String orderType, String orderStatus, String receivedAt, Double totalPrice, Double deliveryFee, String applicationStatus, String adminMessage, String updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.orderType = orderType;
        this.orderStatus = orderStatus;
        this.receivedAt = receivedAt;
        this.totalPrice = totalPrice;
        this.deliveryFee = deliveryFee;
        this.applicationStatus = applicationStatus;
        this.adminMessage = adminMessage;
        this.updatedAt = updatedAt;
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
