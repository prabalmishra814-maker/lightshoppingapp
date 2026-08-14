package com.amstudio.lightbasket.utils;

import android.content.Context;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.amstudio.lightbasket.api.SessionManager;
import com.amstudio.lightbasket.api.SupabaseClient;
import com.amstudio.lightbasket.models.ProductModel;
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
        addToCart(context, product, 1, callback);
    }

    public static void addToCart(Context context, ProductModel product, int quantity, CartCallback callback) {
        SessionManager sessionManager = new SessionManager(context);
        String userId = sessionManager.getUserId();

        if (userId.isEmpty()) {
            Toast.makeText(context, "Please login to add to cart", Toast.LENGTH_SHORT).show();
            if (callback != null) callback.onFailure("Not logged in");
            return;
        }

        String rawPrice = product.getSellingPrice() != null ? product.getSellingPrice() : (product.getPrice() != null ? product.getPrice() : "0");
        String rawMrp = product.getMrp() != null ? product.getMrp() : (product.getMainPrice() != null ? product.getMainPrice() : rawPrice);
        
        double priceValue = PriceUtils.parsePrice(rawPrice);
        double mrpValue = PriceUtils.parsePrice(rawMrp);

        String pid = product.getProductId();
        if (pid == null || pid.isEmpty()) {
            android.util.Log.e("CartHelper", "CRITICAL: Product ID is null for " + product.getProductName());
            if (callback != null) callback.onFailure("Invalid Product ID");
            return;
        }

        Map<String, Object> cartData = new HashMap<>();
        cartData.put("user_id", userId);
        cartData.put("product_id", pid);
        cartData.put("product_name", product.getProductName() != null ? product.getProductName() : "Unknown Product");
        cartData.put("product_price", String.valueOf(priceValue));
        cartData.put("product_mrp", String.valueOf(mrpValue)); 
        cartData.put("product_image", product.getProductImage() != null ? product.getProductImage() : "");
        cartData.put("quantity", quantity);

        android.util.Log.d("CartHelper", "Sending Cart Data: " + cartData.toString());

        String authHeader = "Bearer " + sessionManager.getToken();

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
                    CartManager.getInstance().addItem(product, quantity);
                    if (callback != null) callback.onSuccess();
                } else {
                    String error = "Error " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            if (errorBody.contains("message")) {
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

