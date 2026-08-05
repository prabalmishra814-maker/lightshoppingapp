package com.example.lightshop;

public class OrderModel {
    private String orderId;
    private String date;
    private String productName;
    private String price;
    private int quantity;
    private String status;
    private int imageRes;

    public OrderModel(String orderId, String date, String productName, String price, int quantity, String status, int imageRes) {
        this.orderId = orderId;
        this.date = date;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.status = status;
        this.imageRes = imageRes;
    }

    public String getOrderId() { return orderId; }
    public String getDate() { return date; }
    public String getProductName() { return productName; }
    public String getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getStatus() { return status; }
    public int getImageRes() { return imageRes; }
}
