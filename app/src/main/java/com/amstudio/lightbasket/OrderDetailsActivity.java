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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.bumptech.glide.Glide;
import com.amstudio.lightbasket.utils.StatusBarUtils;
import com.google.android.material.button.MaterialButton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailsActivity extends AppCompatActivity {

    private TextView tvOrderNumber, tvCustomerName, tvFullAddress, tvProductName, tvPriceQty, tvPaymentMethod, tvTotalAmount;
    private ImageView ivProduct;
    private ImageView dotConfirmed, dotShipped, dotDelivered;
    private View linePlaced, lineConfirmed, lineShipped;
    private TextView labelConfirmed, labelShipped, labelDelivered;
    private TextView subConfirmed, subShipped, subDelivered;
    private MaterialButton btnViewOnMap, btnCancelOrder, btnReplaceProduct;
    private SwipeRefreshLayout swipeRefresh;
    private String currentOrderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        StatusBarUtils.applyWhiteStatusBar(this);
        setContentView(R.layout.activity_order_details);

        initViews();
        handleIntentData();

        swipeRefresh.setOnRefreshListener(this::fetchOrderDetails);

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
        btnReplaceProduct = findViewById(R.id.btnReplaceProduct);
        swipeRefresh = findViewById(R.id.swipe_refresh);

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
        String size = intent.getStringExtra("product_size");
        
        String priceQtyText = "₹" + price + " | Qty: " + qty;
        if (size != null && !size.isEmpty() && !size.equalsIgnoreCase("null") && !size.equals("0")) {
            priceQtyText += " | Size: " + size;
        }
        tvPriceQty.setText(priceQtyText);

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

        // Replacement Logic: Allow only if Delivered
        if (status != null && status.equalsIgnoreCase("Delivered")) {
            btnReplaceProduct.setVisibility(View.VISIBLE);
            btnReplaceProduct.setOnClickListener(v -> showReplacementReasonDialog());
        } else {
            btnReplaceProduct.setVisibility(View.GONE);
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

    private void fetchOrderDetails() {
        if (currentOrderId == null) {
            swipeRefresh.setRefreshing(false);
            return;
        }

        String authHeader = "Bearer " + new com.amstudio.lightbasket.api.SessionManager(this).getToken();
        Map<String, String> filters = new HashMap<>();
        filters.put("id", "eq." + currentOrderId);
        filters.put("select", "*");

        com.amstudio.lightbasket.api.SupabaseClient.getApiService().fetchDataWithFilters(
                "orders",
                com.amstudio.lightbasket.api.SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                filters
        ).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Map<String, Object> data = response.body().get(0);
                    updateUI(data);
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(OrderDetailsActivity.this, "Failed to refresh data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(Map<String, Object> data) {
        if (data == null) return;

        tvOrderNumber.setText(String.valueOf(data.get("order_number")));
        tvCustomerName.setText(String.valueOf(data.get("customer_name")));
        tvPaymentMethod.setText(String.valueOf(data.get("payment_method")));
        tvTotalAmount.setText("₹" + String.valueOf(data.get("final_amount")));

        // Parse Address
        Object addrObj = data.get("delivery_address");
        if (addrObj instanceof Map) {
            Map<String, Object> addrMap = (Map<String, Object>) addrObj;
            tvFullAddress.setText(String.valueOf(addrMap.get("full_address")));
            
            Object latObj = addrMap.get("latitude");
            Object lngObj = addrMap.get("longitude");
            double lat = 0.0, lng = 0.0;
            if (latObj instanceof Number) lat = ((Number) latObj).doubleValue();
            if (lngObj instanceof Number) lng = ((Number) lngObj).doubleValue();

            if (lat != 0.0 && lng != 0.0) {
                btnViewOnMap.setVisibility(View.VISIBLE);
                final double finalLat = lat;
                final double finalLng = lng;
                btnViewOnMap.setOnClickListener(v -> {
                    String uri = "geo:" + finalLat + "," + finalLng + "?q=" + finalLat + "," + finalLng + "(Customer Location)";
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(mapIntent);
                });
            } else {
                btnViewOnMap.setVisibility(View.GONE);
            }
        }

        // Parse Products from order_items
        Object itemsObj = data.get("order_items");
        if (itemsObj instanceof List) {
            List<Map<String, Object>> items = (List<Map<String, Object>>) itemsObj;
            String targetProductName = tvProductName.getText().toString();
            Map<String, Object> targetItem = null;

            // Try to find the item that matches the current displayed product name
            for (Map<String, Object> item : items) {
                if (targetProductName.equals(String.valueOf(item.get("product_name")))) {
                    targetItem = item;
                    break;
                }
            }

            // Fallback to first item if no match found
            if (targetItem == null && !items.isEmpty()) {
                targetItem = items.get(0);
            }

            if (targetItem != null) {
                tvProductName.setText(String.valueOf(targetItem.get("product_name")));
                String price = String.valueOf(targetItem.get("price_per_unit"));
                
                int qty = 1;
                Object qtyObj = targetItem.get("quantity");
                if (qtyObj instanceof Number) qty = ((Number) qtyObj).intValue();
                
                String size = targetItem.get("product_size") != null ? String.valueOf(targetItem.get("product_size")) : "";
                String priceQtyText = "₹" + price + " | Qty: " + qty;
                if (!size.isEmpty() && !size.equalsIgnoreCase("null") && !size.equals("0")) {
                    priceQtyText += " | Size: " + size;
                }
                tvPriceQty.setText(priceQtyText);

                String imageUrl = String.valueOf(targetItem.get("product_image"));
                Glide.with(this).load(imageUrl).placeholder(R.drawable.ic_headphones).into(ivProduct);
            }
        }

        String status = String.valueOf(data.get("order_status"));
        updateStepper(status);

        // Cancellation Logic
        if (status != null && (status.equalsIgnoreCase("Pending") || status.equalsIgnoreCase("Confirmed"))) {
            btnCancelOrder.setVisibility(View.VISIBLE);
            btnCancelOrder.setOnClickListener(v -> showCancelReasonDialog());
        } else {
            btnCancelOrder.setVisibility(View.GONE);
        }

        // Replacement Logic
        if (status != null && status.equalsIgnoreCase("Delivered")) {
            btnReplaceProduct.setVisibility(View.VISIBLE);
            btnReplaceProduct.setOnClickListener(v -> showReplacementReasonDialog());
        } else {
            btnReplaceProduct.setVisibility(View.GONE);
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
            Toast.makeText(this, "Something went wrong. Please contact the developer.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnCancelOrder.setEnabled(false);
        btnCancelOrder.setText("CANCELLING...");

        String authHeader = "Bearer " + new com.amstudio.lightbasket.api.SessionManager(this).getToken();
        
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("order_status", "Cancelled");
        updateData.put("cancel_reason", reason);

        Map<String, String> filters = new HashMap<>();
        filters.put("id", "eq." + currentOrderId);

        com.amstudio.lightbasket.api.SupabaseClient.getApiService().updateDataByColumn(
                "orders",
                com.amstudio.lightbasket.api.SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                filters,
                updateData
        ).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OrderDetailsActivity.this, "Order Cancelled Successfully", Toast.LENGTH_SHORT).show();
                    updateStepper("Cancelled");
                    btnCancelOrder.setVisibility(View.GONE);
                } else {
                    btnCancelOrder.setEnabled(true);
                    btnCancelOrder.setText("Cancel Order");
                    Toast.makeText(OrderDetailsActivity.this, "Something went wrong. Please contact the developer.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                btnCancelOrder.setEnabled(true);
                btnCancelOrder.setText("Cancel Order");
                Toast.makeText(OrderDetailsActivity.this, "Something went wrong. Please contact the developer.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showReplacementReasonDialog() {
        String[] reasons = {"Defective/Damaged product", "Wrong item received", "Size/Fit issue", "Quality not as expected", "Other"};

        new AlertDialog.Builder(this)
                .setTitle("Select Reason for Replacement")
                .setItems(reasons, (dialog, which) -> {
                    String reason = reasons[which];
                    confirmReplacement(reason);
                })
                .setNegativeButton("Back", null)
                .show();
    }

    private void confirmReplacement(String reason) {
        new AlertDialog.Builder(this)
                .setTitle("Request Replacement?")
                .setMessage("Are you sure you want to request a replacement for reason: " + reason + "?")
                .setPositiveButton("Yes, Request", (dialog, which) -> submitReplacementToSupabase(reason))
                .setNegativeButton("No", null)
                .show();
    }

    private void submitReplacementToSupabase(String reason) {
        if (currentOrderId == null || currentOrderId.isEmpty()) {
            Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnReplaceProduct.setEnabled(false);
        btnReplaceProduct.setText("PROCESSING...");

        com.amstudio.lightbasket.api.SessionManager sessionManager = new com.amstudio.lightbasket.api.SessionManager(this);
        String authHeader = "Bearer " + sessionManager.getToken();

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("order_status", "Replacement Request");
        updateData.put("replacement_reason", reason); // Optional: adding reason to main table

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
                    Toast.makeText(OrderDetailsActivity.this, "Replacement Request Submitted", Toast.LENGTH_SHORT).show();
                    btnReplaceProduct.setVisibility(View.GONE);
                    // Update UI stepper or label if needed
                    labelDelivered.setText("Replacement Requested");
                } else {
                    btnReplaceProduct.setEnabled(true);
                    btnReplaceProduct.setText("Replace Product");
                    Toast.makeText(OrderDetailsActivity.this, "Failed to submit request", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                btnReplaceProduct.setEnabled(true);
                btnReplaceProduct.setText("Replace Product");
                Toast.makeText(OrderDetailsActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStepper(String status) {
        if (status == null) return;
        status = status.trim();

        int activeColor = ContextCompat.getColor(this, R.color.savings_green);
        ColorStateList activeTint = ColorStateList.valueOf(activeColor);

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
