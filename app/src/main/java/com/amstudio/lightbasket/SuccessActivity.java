package com.amstudio.lightbasket;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.amstudio.lightbasket.utils.StatusBarUtils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.applyWhiteStatusBar(this);
        setContentView(R.layout.activity_success);

        String orderId = getIntent().getStringExtra("order_id");
        if (orderId != null && !orderId.isEmpty()) {
            TextView tvOrderId = findViewById(R.id.tv_order_id);
            tvOrderId.setText(getString(R.string.order_id_label, orderId));
        }

        setEstimatedDelivery();

        findViewById(R.id.btn_go_home).setOnClickListener(v -> {
            Intent intent = new Intent(SuccessActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.btn_view_orders).setOnClickListener(v -> {
            // Since MyOrdersFragment is inside HomeActivity, we tell HomeActivity to switch tab
            Intent intent = new Intent(SuccessActivity.this, HomeActivity.class);
            intent.putExtra("navigate_to", "orders");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Handle back press
        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(SuccessActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setEstimatedDelivery() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 24);
        
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM", Locale.getDefault());
        SimpleDateFormat timeSdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        
        String date = sdf.format(calendar.getTime());
        String time = timeSdf.format(calendar.getTime());
        
        TextView tvDelivery = findViewById(R.id.tv_delivery_date);
        tvDelivery.setText("By " + date + ", " + time);
    }
}

