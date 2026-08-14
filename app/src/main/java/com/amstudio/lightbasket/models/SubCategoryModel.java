package com.amstudio.lightbasket.models;

import com.google.gson.annotations.SerializedName;

public class SubCategoryModel {
    @SerializedName("id")
    private long id;

    @SerializedName("category_id")
    private long categoryId;

    @SerializedName("sub_category_name")
    private String subCategoryName;

    @SerializedName("sub_category_image")
    private String subCategoryImage;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getCategoryId() { return categoryId; }
    public void setCategoryId(long categoryId) { this.categoryId = categoryId; }

    public String getSubCategoryName() { return subCategoryName; }
    public void setSubCategoryName(String subCategoryName) { this.subCategoryName = subCategoryName; }

    public String getSubCategoryImage() { return subCategoryImage; }
    public void setSubCategoryImage(String subCategoryImage) { this.subCategoryImage = subCategoryImage; }
}

