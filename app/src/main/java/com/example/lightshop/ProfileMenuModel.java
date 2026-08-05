package com.example.lightshop;

public class ProfileMenuModel {
    private int id;
    private String title;
    private int iconResId;
    private int textColorResId;
    private int iconColorResId;

    public ProfileMenuModel(int id, String title, int iconResId) {
        this(id, title, iconResId, R.color.text_primary, R.color.icon_color);
    }

    public ProfileMenuModel(int id, String title, int iconResId, int textColorResId, int iconColorResId) {
        this.id = id;
        this.title = title;
        this.iconResId = iconResId;
        this.textColorResId = textColorResId;
        this.iconColorResId = iconColorResId;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public int getIconResId() { return iconResId; }
    public int getTextColorResId() { return textColorResId; }
    public int getIconColorResId() { return iconColorResId; }
}
