package com.example.lightshop;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import com.example.lightshop.api.SessionManager;
import com.example.lightshop.api.SupabaseClient;
import com.example.lightshop.models.CartItem;
import com.example.lightshop.utils.CartManager;
import com.example.lightshop.utils.PriceUtils;
import com.example.lightshop.utils.StatusBarUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {

    private TextView tvTotalPriceMrp, tvTotalDiscount, tvTotalSellingPrice, tvSavingsMessage, tvDeliveryCharges;
    private android.widget.Button btnContinue;
    private SessionManager sessionManager;
    private FusedLocationProviderClient fusedLocationClient;
    private Double latitude = null;
    private Double longitude = null;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.applyWhiteStatusBar(this);
        setContentView(R.layout.activity_checkout);

        sessionManager = new SessionManager(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        
        initViews();
        updatePriceDetails();
        fetchUserAddress();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnChangeAddress).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(CheckoutActivity.this, AddAddressActivity.class);
            startActivity(intent);
        });
        btnContinue.setOnClickListener(v -> checkLocationPermissionAndPlaceOrder());
    }

    private void initViews() {
        tvTotalPriceMrp = findViewById(R.id.tvTotalPriceMrp);
        tvTotalDiscount = findViewById(R.id.tvTotalDiscount);
        tvTotalSellingPrice = findViewById(R.id.tvTotalSellingPrice);
        tvSavingsMessage = findViewById(R.id.tvSavingsMessage);
        tvDeliveryCharges = findViewById(R.id.tvDeliveryCharges);
        btnContinue = findViewById(R.id.btnContinue);
    }

    private void checkLocationPermissionAndPlaceOrder() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fetchLocationAndPlaceOrder();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // Even if permission is denied, we still place the order (just without GPS)
            fetchLocationAndPlaceOrder();
        }
    }

    private void fetchLocationAndPlaceOrder() {
        btnContinue.setEnabled(false);
        btnContinue.setText("FETCHING LOCATION...");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // No permission, proceed to place order directly
            placeOrder();
            return;
        }

        fusedLocationClient.getLastLocation().addOnCompleteListener(this, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Location location = task.getResult();
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
            // Proceed to place order whether location was found or not
            placeOrder();
        });
    }

    private void placeOrder() {
        String userId = sessionManager.getUserId();
        if (userId.isEmpty()) return;

        String authHeader = "Bearer " + sessionManager.getToken();
        
        btnContinue.setEnabled(false);
        btnContinue.setText("PLACING ORDER...");

        // Prepare Order Data
        CartManager manager = CartManager.getInstance();
        int deliveryFee = 0; 
        int finalAmount = manager.getTotalSellingPrice() + deliveryFee;

        Map<String, Object> orderData = new HashMap<>();
        orderData.put("user_id", userId);
        String orderNumber = "#LS-" + (1000 + new Random().nextInt(9000));
        orderData.put("order_number", orderNumber);
        orderData.put("customer_name", ((TextView)findViewById(R.id.tvCustomerName)).getText().toString());
        orderData.put("customer_email", sessionManager.getUserEmail());
        orderData.put("customer_phone", ((TextView)findViewById(R.id.tvPhone)).getText().toString());
        
        // Detailed Address Snapshot
        String fullName = ((TextView)findViewById(R.id.tvCustomerName)).getText().toString();
        String addressLines = ((TextView)findViewById(R.id.tvAddressLines)).getText().toString();
        String phone = ((TextView)findViewById(R.id.tvPhone)).getText().toString();

        Map<String, Object> addressMap = new HashMap<>();
        addressMap.put("full_name", fullName);
        addressMap.put("full_address", addressLines);
        addressMap.put("phone", phone);
        if (latitude != null && longitude != null) {
            addressMap.put("latitude", latitude);
            addressMap.put("longitude", longitude);
        }
        orderData.put("delivery_address", addressMap);

        // Detailed Items Snapshot for Admin
        List<Map<String, Object>> itemsList = new ArrayList<>();
        for (CartItem item : manager.getCartItems()) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("product_id", item.getProduct().getProductId());
            itemMap.put("product_name", item.getProduct().getProductName());
            
            int price = PriceUtils.parsePriceInt(item.getProduct().getSellingPrice());
            
            itemMap.put("price_per_unit", price);
            itemMap.put("quantity", item.getQuantity());
            itemMap.put("item_total", price * item.getQuantity());
            itemMap.put("product_image", item.getProduct().getProductImage());
            itemMap.put("item_status", "Pending"); 
            itemsList.add(itemMap);
        }
        orderData.put("order_items", itemsList);

        // Overall Totals
        orderData.put("total_mrp", manager.getTotalMrp());
        orderData.put("total_discount", manager.getTotalDiscount());
        orderData.put("delivery_fee", deliveryFee);
        orderData.put("final_amount", finalAmount);
        
        // Status & Payment Details
        orderData.put("order_status", "Pending"); 
        orderData.put("payment_method", "COD");
        orderData.put("payment_status", "Pending");
        orderData.put("payment_type", "Cash on Delivery");

        // Step 1: Insert into 'orders' table
        SupabaseClient.getApiService().addData(
                "orders",
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "return=representation",
                orderData
        ).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful()) {
                    // Step 2: Clear the Cart
                    clearCart(userId, authHeader, orderNumber);
                } else {
                    btnContinue.setEnabled(true);
                    btnContinue.setText("PLACE ORDER");
                    Toast.makeText(CheckoutActivity.this, "Order Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                btnContinue.setEnabled(true);
                btnContinue.setText("PLACE ORDER");
                Toast.makeText(CheckoutActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearCart(String userId, String authHeader, String orderNumber) {
        Map<String, String> filters = new HashMap<>();
        filters.put("user_id", "eq." + userId);

        SupabaseClient.getApiService().deleteDataByFilters(
                "cart",
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                filters
        ).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                CartManager.getInstance().getCartItems().clear();
                showSuccessActivity(orderNumber);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Even if clearing cart fails, order is placed
                CartManager.getInstance().getCartItems().clear();
                showSuccessActivity(orderNumber);
            }
        });
    }

    private void showSuccessActivity(String orderNumber) {
        android.content.Intent intent = new android.content.Intent(this, SuccessActivity.class);
        intent.putExtra("order_id", orderNumber);
        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void updatePriceDetails() {
        CartManager manager = CartManager.getInstance();
        int deliveryFee = 0;
        int totalSelling = manager.getTotalSellingPrice() + deliveryFee;

        tvTotalPriceMrp.setText(PriceUtils.formatPrice(manager.getTotalMrp()));
        tvTotalDiscount.setText("-" + PriceUtils.formatPrice(manager.getTotalDiscount()));
        tvDeliveryCharges.setText("FREE");
        tvDeliveryCharges.setTextColor(getResources().getColor(R.color.savings_green));
        
        tvTotalSellingPrice.setText(PriceUtils.formatPrice(totalSelling));
        tvSavingsMessage.setText("You will save " + PriceUtils.formatPrice(manager.getTotalDiscount()) + " on this order");
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchUserAddress();
    }

    private void fetchUserAddress() {
        String userId = sessionManager.getUserId();
        if (userId == null || userId.isEmpty()) return;

        String authHeader = "Bearer " + sessionManager.getToken();
        Map<String, String> filters = new HashMap<>();
        filters.put("uid", "eq." + userId);
        filters.put("select", "address");

        SupabaseClient.getApiService().fetchDataWithFilters(
                "Users",
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                filters
        ).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Object addressObj = response.body().get(0).get("address");
                    if (addressObj instanceof Map) {
                        Map<String, Object> address = (Map<String, Object>) addressObj;
                        updateAddressUI(address);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {}
        });
    }

    private void updateAddressUI(Map<String, Object> address) {
        try {
            String name = (String) address.get("name");
            String house = (String) address.get("house_no");
            String road = (String) address.get("road_name");
            String city = (String) address.get("city");
            String state = (String) address.get("state");
            String pincode = (String) address.get("pincode");
            String phone = (String) address.get("number");

            ((TextView) findViewById(R.id.tvCustomerName)).setText(name);
            String fullAddress = house + ", " + road + "\n" + city + ", " + state + " - " + pincode;
            ((TextView) findViewById(R.id.tvAddressLines)).setText(fullAddress);
            ((TextView) findViewById(R.id.tvPhone)).setText("Phone: " + phone);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
