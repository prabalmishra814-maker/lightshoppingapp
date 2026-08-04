package com.example.lightshop;

public class Category {
    private String name;
    private String itemCount;
    private int iconRes;
    private boolean isSelected;

    public Category(String name, int iconRes) {
        this.name = name;
        this.iconRes = iconRes;
        this.isSelected = false;
    }

    public Category(String name, String itemCount, int iconRes) {
        this.name = name;
        this.itemCount = itemCount;
        this.iconRes = iconRes;
        this.isSelected = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItemCount() {
        return itemCount;
    }

    public void setItemCount(String itemCount) {
        this.itemCount = itemCount;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
