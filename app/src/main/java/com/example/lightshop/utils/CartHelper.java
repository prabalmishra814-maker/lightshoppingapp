package com.example.lightshop.utils;

import android.content.Context;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.lightshop.api.SessionManager;
import com.example.lightshop.api.SupabaseClient;
import com.example.lightshop.models.ProductModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartHelper {

    public interface CartCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public static void addToCart(Context context, ProductModel product, CartCallback callback) {
        SessionManager sessionManager = new SessionManager(context);
        String userId = sessionManager.getUserId();

        if (userId.isEmpty()) {
            Toast.makeText(context, "Please login to add to cart", Toast.LENGTH_SHORT).show();
            if (callback != null) callback.onFailure("Not logged in");
            return;
        }

        // Map to DB columns in 'cart' table as per requirement:
        // product_price (Cart) = product_price (Product) -> product.getPrice()
        // product_mrp (Cart) = product_main_price (Product) -> product.getMainPrice()
        
        String rawPrice = product.getPrice(); 
        String rawMrp = product.getMainPrice();
        
        if (rawPrice == null || rawPrice.isEmpty()) rawPrice = "0";
        if (rawMrp == null || rawMrp.isEmpty()) rawMrp = rawPrice;
        
        double priceValue = PriceUtils.parsePrice(rawPrice);
        double mrpValue = PriceUtils.parsePrice(rawMrp);

        // Ensure we send valid strings for required text columns
        String pid = product.getProductId();
        if (pid == null || pid.isEmpty()) {
            android.util.Log.e("CartHelper", "CRITICAL: Product ID is null for " + product.getProductName());
            pid = "00000000-0000-0000-0000-000000000000"; 
        }

        Map<String, Object> cartData = new HashMap<>();
        cartData.put("user_id", userId);
        cartData.put("product_id", pid);
        cartData.put("product_name", product.getProductName() != null ? product.getProductName() : "Unknown Product");
        cartData.put("product_price", String.valueOf(priceValue));
        cartData.put("product_mrp", String.valueOf(mrpValue)); 
        cartData.put("product_image", product.getProductImage() != null ? product.getProductImage() : "");
        cartData.put("quantity", 1);

        android.util.Log.d("CartHelper", "Sending Cart Data: " + cartData.toString());

        String authHeader = "Bearer " + sessionManager.getToken();

        // Perform insertion with UPSERT logic
        SupabaseClient.getApiService().addData(
                "cart",
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "resolution=merge-duplicates,return=representation",
                cartData
        ).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(@NonNull Call<List<Map<String, Object>>> call, @NonNull Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful()) {
                    // Update Local CartManager
                    CartManager.getInstance().addItem(product, 1);
                    if (callback != null) callback.onSuccess();
                } else {
                    String error = "Error " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            // Supabase usually returns {"message": "...", "hint": "...", "details": "..."}
                            if (errorBody.contains("message")) {
                                // Simple extraction if GSON isn't handy or to keep it light
                                error += ": " + errorBody;
                            }
                        }
                    } catch (Exception ignored) {}
                    if (callback != null) callback.onFailure(error);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Map<String, Object>>> call, @NonNull Throwable t) {
                if (callback != null) callback.onFailure(t.getMessage());
            }
        });
    }
}
