package com.example.lightshop.models;

import com.google.gson.annotations.SerializedName;

public class ProductModel {
    @SerializedName("PRODUCT_ID")
    private String productId;

    @SerializedName("product_name")
    private String productName;

    @SerializedName("product_image")
    private String productImage;

    @SerializedName("product_short_description")
    private String shortDescription;

    @SerializedName("product_category")
    private String category;

    @SerializedName("product_price")
    private String price;

    @SerializedName("product_main_price")
    private String mainPrice;

    @SerializedName("product_size")
    private String size;

    @SerializedName("product_description")
    private String description;

    @SerializedName("product_sub_category")
    private String subCategory;

    @SerializedName("product_stock")
    private String stock;

    @SerializedName("product_brand")
    private String brand;

    @SerializedName("product_mrp")
    private String mrp;

    @SerializedName("product_selling_price")
    private String sellingPrice;

    @SerializedName("product_image2")
    private String productImage2;

    @SerializedName("product_image3")
    private String productImage3;

    @SerializedName("product_image4")
    private String productImage4;

    @SerializedName("product_image5")
    private String productImage5;

    public ProductModel() {
    }

    // Getters and Setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }

    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getMainPrice() { return mainPrice; }
    public void setMainPrice(String mainPrice) { this.mainPrice = mainPrice; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSubCategory() { return subCategory; }
    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }

    public String getStock() { return stock; }
    public void setStock(String stock) { this.stock = stock; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getMrp() { return mrp; }
    public void setMrp(String mrp) { this.mrp = mrp; }

    public String getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(String sellingPrice) { this.sellingPrice = sellingPrice; }

    public String getProductImage2() { return productImage2; }
    public void setProductImage2(String productImage2) { this.productImage2 = productImage2; }

    public String getProductImage3() { return productImage3; }
    public void setProductImage3(String productImage3) { this.productImage3 = productImage3; }

    public String getProductImage4() { return productImage4; }
    public void setProductImage4(String productImage4) { this.productImage4 = productImage4; }

    public String getProductImage5() { return productImage5; }
    public void setProductImage5(String productImage5) { this.productImage5 = productImage5; }
}
