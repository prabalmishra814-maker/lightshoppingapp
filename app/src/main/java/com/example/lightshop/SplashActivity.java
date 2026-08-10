package com.example.lightshop;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lightshop.api.SessionManager;
import com.example.lightshop.api.SupabaseClient;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize Supabase Client with Context for Authenticator
        SupabaseClient.init(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SessionManager sessionManager = new SessionManager(SplashActivity.this);
            if (sessionManager.isLoggedIn()) {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, AuthActivity.class));
            }
            finish();
        }, 2000); // 2 seconds delay
    }
}
