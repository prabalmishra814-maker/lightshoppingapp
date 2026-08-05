package com.example.lightshop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lightshop.api.SessionManager;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SessionManager sessionManager = new SessionManager(this);

        TextView tvName = findViewById(R.id.tv_profile_name);
        TextView tvEmail = findViewById(R.id.tv_profile_email);

        tvName.setText(sessionManager.getUserName());
        tvEmail.setText(sessionManager.getUserEmail());

        findViewById(R.id.btn_logout).setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(ProfileActivity.this, AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
