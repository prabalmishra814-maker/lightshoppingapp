package com.example.lightshop.api;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "LightShopSession";
    private static final String KEY_TOKEN = "access_token";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveSession(String token, String email, String name) {
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_NAME, name);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getString(KEY_TOKEN, null) != null;
    }

    public String getToken() {
        return pref.getString(KEY_TOKEN, null);
    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "User");
    }

    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, "");
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
