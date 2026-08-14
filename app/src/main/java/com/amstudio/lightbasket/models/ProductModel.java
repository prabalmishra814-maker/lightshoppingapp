package com.amstudio.lightbasket.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class  ProductModel implements Serializable {
    @SerializedName(value = "product_id", alternate = {"id", "PRODUCT_ID"})
    private String productId;

    @SerializedName(value = "product_name", alternate = {"name"})
    private String productName;

    @SerializedName(value = "product_image", alternate = {"image"})
    private String productImage;

    @SerializedName("product_short_description")
    private String shortDescription;

    @SerializedName("product_category")
    private String category;

    @SerializedName(value = "product_price", alternate = {"price"})
    private String price;

    @SerializedName(value = "product_main_price", alternate = {"main_price"})
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

    @SerializedName(value = "product_mrp", alternate = {"mrp"})
    private String mrp;

    @SerializedName(value = "product_selling_price", alternate = {"selling_price"})
    private String sellingPrice;

    @SerializedName("product_image2")
    private String productImage2;

    @SerializedName("product_image3")
    private String productImage3;

    @SerializedName("product_image4")
    private String productImage4;

    @SerializedName("product_image5")
    private String productImage5;

    @SerializedName("product_rating")
    private String rating;

    @SerializedName("product_reviews_count")
    private String reviewsCount;

    @SerializedName("product_sold_count")
    private String soldCount;

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

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public String getReviewsCount() { return reviewsCount; }
    public void setReviewsCount(String reviewsCount) { this.reviewsCount = reviewsCount; }

    public String getSoldCount() { return soldCount; }
    public void setSoldCount(String soldCount) { this.soldCount = soldCount; }
}

