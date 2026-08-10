package com.example.lightshop;

import android.app.Application;
import com.example.lightshop.api.SupabaseClient;

public class LightShopApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SupabaseClient.init(this);
    }
}
