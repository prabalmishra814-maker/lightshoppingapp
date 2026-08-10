package com.example.lightshop.api;

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

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
