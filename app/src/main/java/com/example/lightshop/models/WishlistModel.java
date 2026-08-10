package com.example.lightshop.models;

import com.google.gson.annotations.SerializedName;

public class WishlistModel {
    @SerializedName("id")
    private long id;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("product_id")
    private String productId;

    @SerializedName("product_name")
    private String productName;

    @SerializedName("product_price")
    private String productPrice;

    @SerializedName("product_image")
    private String productImage;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductPrice() { return productPrice; }
    public void setProductPrice(String productPrice) { this.productPrice = productPrice; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }
}
