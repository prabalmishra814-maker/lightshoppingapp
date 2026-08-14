package com.amstudio.lightbasket;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.amstudio.lightbasket.api.SessionManager;
import com.amstudio.lightbasket.api.SupabaseClient;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize Supabase Client with Context for Authenticator
        SupabaseClient.init(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SessionManager sessionManager = new SessionManager(SplashActivity.this);
            if (!sessionManager.isLoggedIn()) {
                // Bypass login for development: set dummy session
                sessionManager.saveSession("dummy_token_bypass", "dummy_refresh_token", "dummy_uid", "guest@example.com", "Guest User", "");
            }
            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            finish();
        }, 2000); // 2 seconds delay
    }
}

