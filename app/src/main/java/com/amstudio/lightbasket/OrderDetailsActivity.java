package com.amstudio.lightbasket;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.amstudio.lightbasket.utils.StatusBarUtils;
import com.google.android.material.button.MaterialButton;
import java.util.HashMap;
import java.util.Map;
import okhttp3.ResponseBody;

public class OrderDetailsActivity extends AppCompatActivity {

    private TextView tvOrderNumber, tvCustomerName, tvFullAddress, tvProductName, tvPriceQty, tvPaymentMethod, tvTotalAmount;
    private ImageView ivProduct;
    private ImageView dotConfirmed, dotShipped, dotDelivered;
    private View linePlaced, lineConfirmed, lineShipped;
    private TextView labelConfirmed, labelShipped, labelDelivered;
    private TextView subConfirmed, subShipped, subDelivered;
    private MaterialButton btnViewOnMap, btnCancelOrder;
    private String currentOrderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        StatusBarUtils.applyWhiteStatusBar(this);
        setContentView(R.layout.activity_order_details);

        initViews();
        handleIntentData();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnHelp).setOnClickListener(v -> {
            Toast.makeText(this, "Support will contact you soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void initViews() {
        tvOrderNumber = findViewById(R.id.tvOrderNumber);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvFullAddress = findViewById(R.id.tvFullAddress);
        tvProductName = findViewById(R.id.tvProductName);
        tvPriceQty = findViewById(R.id.tvPriceQty);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        ivProduct = findViewById(R.id.ivProduct);
        btnViewOnMap = findViewById(R.id.btnViewOnMap);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);

        dotConfirmed = findViewById(R.id.dot_confirmed);
        dotShipped = findViewById(R.id.dot_shipped);
        dotDelivered = findViewById(R.id.dot_delivered);

        linePlaced = findViewById(R.id.line_placed);
        lineConfirmed = findViewById(R.id.line_confirmed);
        lineShipped = findViewById(R.id.line_shipped);

        labelConfirmed = findViewById(R.id.tv_label_confirmed);
        labelShipped = findViewById(R.id.tv_label_shipped);
        labelDelivered = findViewById(R.id.tv_label_delivered);

        subConfirmed = findViewById(R.id.tv_sub_confirmed);
        subShipped = findViewById(R.id.tv_sub_shipped);
        subDelivered = findViewById(R.id.tv_sub_delivered);
    }

    private void handleIntentData() {
        Intent intent = getIntent();
        if (intent == null) return;

        currentOrderId = intent.getStringExtra("order_id");
        tvOrderNumber.setText(intent.getStringExtra("order_number"));
        tvCustomerName.setText(intent.getStringExtra("customer_name"));
        tvFullAddress.setText(intent.getStringExtra("full_address"));
        tvProductName.setText(intent.getStringExtra("product_name"));
        
        String price = intent.getStringExtra("price");
        int qty = intent.getIntExtra("quantity", 1);
        tvPriceQty.setText("₹" + price + " | Qty: " + qty);

        tvPaymentMethod.setText(intent.getStringExtra("payment_method"));
        tvTotalAmount.setText("₹" + intent.getStringExtra("final_amount"));

        String imageUrl = intent.getStringExtra("image_url");
        Glide.with(this).load(imageUrl).placeholder(R.drawable.ic_headphones).into(ivProduct);

        String status = intent.getStringExtra("status");
        updateStepper(status);

        // Cancellation Logic: Allow only if Pending or Confirmed
        if (status != null && (status.equalsIgnoreCase("Pending") || status.equalsIgnoreCase("Confirmed"))) {
            btnCancelOrder.setVisibility(View.VISIBLE);
            btnCancelOrder.setOnClickListener(v -> showCancelReasonDialog());
        } else {
            btnCancelOrder.setVisibility(View.GONE);
        }

        double lat = intent.getDoubleExtra("latitude", 0.0);
        double lng = intent.getDoubleExtra("longitude", 0.0);

        if (lat != 0.0 && lng != 0.0) {
            btnViewOnMap.setVisibility(View.VISIBLE);
            btnViewOnMap.setOnClickListener(v -> {
                String uri = "geo:" + lat + "," + lng + "?q=" + lat + "," + lng + "(Customer Location)";
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(mapIntent);
            });
        }
    }

    private void showCancelReasonDialog() {
        String[] reasons = {"Changed my mind", "Found better price elsewhere", "Wrong address selected", "Delayed delivery", "Other"};
        
        new AlertDialog.Builder(this)
                .setTitle("Select Reason to Cancel")
                .setItems(reasons, (dialog, which) -> {
                    String reason = reasons[which];
                    confirmCancellation(reason);
                })
                .setNegativeButton("Back", null)
                .show();
    }

    private void confirmCancellation(String reason) {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Order?")
                .setMessage("Are you sure you want to cancel this order for reason: " + reason + "?")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> cancelOrderOnSupabase(reason))
                .setNegativeButton("No", null)
                .show();
    }

    private void cancelOrderOnSupabase(String reason) {
        if (currentOrderId == null || currentOrderId.isEmpty()) {
            Toast.makeText(this, "Order ID missing", Toast.LENGTH_SHORT).show();
            return;
        }

        btnCancelOrder.setEnabled(false);
        btnCancelOrder.setText("CANCELLING...");

        String authHeader = "Bearer " + new com.amstudio.lightbasket.api.SessionManager(this).getToken();
        
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("order_status", "Cancelled");
        updateData.put("cancel_reason", reason);

        // Filters for the specific order row
        Map<String, String> filters = new HashMap<>();
        filters.put("id", "eq." + currentOrderId);

        com.amstudio.lightbasket.api.SupabaseClient.getApiService().updateDataByColumn(
                "orders",
                com.amstudio.lightbasket.api.SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                filters,
                updateData
        ).enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OrderDetailsActivity.this, "Order Cancelled Successfully", Toast.LENGTH_SHORT).show();
                    updateStepper("Cancelled");
                    btnCancelOrder.setVisibility(View.GONE);
                } else {
                    btnCancelOrder.setEnabled(true);
                    btnCancelOrder.setText("Cancel Order");
                    Toast.makeText(OrderDetailsActivity.this, "Update Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                btnCancelOrder.setEnabled(true);
                btnCancelOrder.setText("Cancel Order");
                Toast.makeText(OrderDetailsActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStepper(String status) {
        if (status == null) return;
        status = status.trim();

        int activeColor = ContextCompat.getColor(this, R.color.savings_green);
        ColorStateList activeTint = ColorStateList.valueOf(activeColor);

        // ALWAYS Reset colors first (Crucial for correct UI)
        resetStepper();

        if (status.equalsIgnoreCase("Confirmed") || status.equalsIgnoreCase("Ready to Ship") || 
            status.equalsIgnoreCase("Shipped") || status.equalsIgnoreCase("Delivered")) {
            setStepActive(dotConfirmed, linePlaced, labelConfirmed, subConfirmed, activeTint);
        }

        if (status.equalsIgnoreCase("Shipped") || status.equalsIgnoreCase("Delivered")) {
            setStepActive(dotShipped, lineConfirmed, labelShipped, subShipped, activeTint);
        }

        if (status.equalsIgnoreCase("Delivered")) {
            setStepActive(dotDelivered, lineShipped, labelDelivered, subDelivered, activeTint);
        }
        
        if (status.equalsIgnoreCase("Cancelled") || status.equalsIgnoreCase("Out of Stock")) {
            int cancelColor = ContextCompat.getColor(this, R.color.status_cancelled);
            ColorStateList cancelTint = ColorStateList.valueOf(cancelColor);
            setStepActive(dotConfirmed, linePlaced, labelConfirmed, subConfirmed, cancelTint);
            labelConfirmed.setText("Order " + status);
            subConfirmed.setText("This item has been marked as " + status.toLowerCase());
        }
    }

    private void resetStepper() {
        int inactiveColor = Color.parseColor("#E0E0E0");
        dotConfirmed.setBackgroundTintList(ColorStateList.valueOf(inactiveColor));
        dotConfirmed.setImageResource(0);
        dotShipped.setBackgroundTintList(ColorStateList.valueOf(inactiveColor));
        dotShipped.setImageResource(0);
        dotDelivered.setBackgroundTintList(ColorStateList.valueOf(inactiveColor));
        dotDelivered.setImageResource(0);
        linePlaced.setBackgroundColor(inactiveColor);
        lineConfirmed.setBackgroundColor(inactiveColor);
        lineShipped.setBackgroundColor(inactiveColor);
    }

    private void setStepActive(ImageView dot, View line, TextView label, TextView subLabel, ColorStateList tint) {
        dot.setBackgroundTintList(tint);
        dot.setImageResource(R.drawable.ic_check);
        dot.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        if (line != null) line.setBackgroundColor(tint.getDefaultColor());
        label.setTextColor(ContextCompat.getColor(this, R.color.black));
        label.setTypeface(null, android.graphics.Typeface.BOLD);
        if (subLabel != null) subLabel.setTextColor(ContextCompat.getColor(this, R.color.text_subtitle));
    }
}

