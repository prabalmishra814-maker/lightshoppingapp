package com.amstudio.lightbasket.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ReplacementRequestModel implements Serializable {
    @SerializedName("order_id")
    private String orderId;

    @SerializedName("order_number")
    private String orderNumber;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("customer_name")
    private String customerName;

    @SerializedName("customer_email")
    private String customerEmail;

    @SerializedName("customer_phone")
    private String customerPhone;

    @SerializedName("full_address")
    private String fullAddress;

    @SerializedName("product_name")
    private String productName;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("reason")
    private String reason;

    @SerializedName("status")
    private String status = "Pending";

    public ReplacementRequestModel() {}

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getFullAddress() { return fullAddress; }
    public void setFullAddress(String fullAddress) { this.fullAddress = fullAddress; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
