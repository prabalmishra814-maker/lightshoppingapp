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
            Intent intent;
            if (sessionManager.isLoggedIn()) {
                intent = new Intent(SplashActivity.this, HomeActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, AuthActivity.class);
            }
            startActivity(intent);
            finish();
        }, 2000); // 2 seconds delay
    }
}

