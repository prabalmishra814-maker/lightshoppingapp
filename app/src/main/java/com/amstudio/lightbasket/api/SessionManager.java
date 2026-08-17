package com.amstudio.lightbasket.api;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "LightShopSession";
    private static final String KEY_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_PROFILE = "user_profile";
    private static final String KEY_USER_PHONE = "user_phone";
    private static final String KEY_RECENTLY_VIEWED = "recently_viewed";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveSession(String token, String refreshToken, String userId, String email, String name, String profileUrl) {
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_PROFILE, profileUrl);
        editor.apply();
    }

    public void updateAccessToken(String token, String refreshToken) {
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.apply();
    }

    public void addToRecentlyViewed(com.amstudio.lightbasket.models.ProductModel product) {
        if (product == null || product.getProductId() == null) return;
        
        java.util.List<com.amstudio.lightbasket.models.ProductModel> list = getRecentlyViewed();
        // Remove if already exists to move it to top
        for (int i = 0; i < list.size(); i++) {
            if (product.getProductId().equals(list.get(i).getProductId())) {
                list.remove(i);
                break;
            }
        }
        list.add(0, product);
        // Limit to 10
        if (list.size() > 10) {
            list.remove(list.size() - 1);
        }
        
        String json = new com.google.gson.Gson().toJson(list);
        editor.putString(KEY_RECENTLY_VIEWED, json);
        editor.apply();
    }

    public java.util.List<com.amstudio.lightbasket.models.ProductModel> getRecentlyViewed() {
        String json = pref.getString(KEY_RECENTLY_VIEWED, null);
        if (json == null) return new java.util.ArrayList<>();
        
        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<java.util.List<com.amstudio.lightbasket.models.ProductModel>>() {}.getType();
        return new com.google.gson.Gson().fromJson(json, type);
    }

    public boolean isLoggedIn() {
        return pref.getString(KEY_TOKEN, null) != null;
    }

    public String getToken() {
        return pref.getString(KEY_TOKEN, null);
    }

    public String getRefreshToken() {
        return pref.getString(KEY_REFRESH_TOKEN, null);
    }

    public String getUserId() {
        return pref.getString(KEY_USER_ID, "");
    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "User");
    }

    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, "");
    }

    public String getUserProfile() {
        return pref.getString(KEY_USER_PROFILE, "");
    }

    public String getUserPhone() {
        return pref.getString(KEY_USER_PHONE, "");
    }

    public void setUserPhone(String phone) {
        editor.putString(KEY_USER_PHONE, phone);
        editor.apply();
    }

    public void setUserName(String name) {
        editor.putString(KEY_USER_NAME, name);
        editor.apply();
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}

