package com.amstudio.lightbasket;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.amstudio.lightbasket.api.SessionManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Using activity_auth as a temporary splash or create a splash layout
        setContentView(R.layout.activity_auth);

        // Simple Splash Screen Logic
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SessionManager sessionManager = new SessionManager(MainActivity.this);
            if (sessionManager.isLoggedIn()) {
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
            } else {
                startActivity(new Intent(MainActivity.this, AuthActivity.class));
            }
            finish();
        }, 2000); // 2 seconds delay
    }
}

