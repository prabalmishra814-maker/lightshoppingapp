package com.example.lightshop;

import android.app.Application;
import com.example.lightshop.api.SupabaseClient;

public class LightShopApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        SupabaseClient.init(this);
    }
}
