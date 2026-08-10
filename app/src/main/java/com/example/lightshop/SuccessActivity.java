package com.example.lightshop;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lightshop.utils.StatusBarUtils;

public class SuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.applyWhiteStatusBar(this);
        setContentView(R.layout.activity_success);

        findViewById(R.id.btn_go_home).setOnClickListener(v -> {
            Intent intent = new Intent(SuccessActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.btn_view_orders).setOnClickListener(v -> {
            Intent intent = new Intent(SuccessActivity.this, HomeActivity.class);
            intent.putExtra("navigate_to", "orders");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
