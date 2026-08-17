package com.amstudio.lightbasket;

public class OrderModel {
    private String orderId;
    private String orderNumber;
    private String date;
    private String productName;
    private String price;
    private int quantity;
    private String status;
    private String imageUrl;
    private String customerName;
    private String customerPhone;
    private String fullAddress;
    private double latitude;
    private double longitude;
    private String paymentMethod;
    private String finalAmount;
    private String replacementReason;
    private String productSize;

    public OrderModel(String orderId, String orderNumber, String date, String productName, String price, int quantity, String status, String imageUrl, String customerName, String customerPhone, String fullAddress, double latitude, double longitude, String paymentMethod, String finalAmount, String replacementReason, String productSize) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.date = date;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.status = status;
        this.imageUrl = imageUrl;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.fullAddress = fullAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.paymentMethod = paymentMethod;
        this.finalAmount = finalAmount;
        this.replacementReason = replacementReason;
        this.productSize = productSize;
    }

    public String getOrderId() { return orderId; }
    public String getOrderNumber() { return orderNumber; }
    public String getDate() { return date; }
    public String getProductName() { return productName; }
    public String getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getStatus() { return status; }
    public String getImageUrl() { return imageUrl; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public String getFullAddress() { return fullAddress; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getFinalAmount() { return finalAmount; }
    public String getReplacementReason() { return replacementReason; }
    public String getProductSize() { return productSize; }
}
