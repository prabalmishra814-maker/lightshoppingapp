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
        addToCart(context, product, 1, null, null, null, callback);
    }

    public static void addToCart(Context context, ProductModel product, int quantity, String selectedSize, String dynamicPrice, String dynamicMrp, CartCallback callback) {
        SessionManager sessionManager = new SessionManager(context);
        String userId = sessionManager.getUserId();

        if (userId.isEmpty()) {
            Toast.makeText(context, "Please login to add to cart", Toast.LENGTH_SHORT).show();
            if (callback != null) callback.onFailure("Not logged in");
            return;
        }

        // Use dynamic price if provided, else fallback to product price
        String rawPrice = (dynamicPrice != null && !dynamicPrice.isEmpty()) ? dynamicPrice : 
                         (product.getSellingPrice() != null ? product.getSellingPrice() : (product.getPrice() != null ? product.getPrice() : "0"));
        
        String rawMrp = (dynamicMrp != null && !dynamicMrp.isEmpty()) ? dynamicMrp : 
                        (product.getMrp() != null ? product.getMrp() : (product.getMainPrice() != null ? product.getMainPrice() : rawPrice));
        
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
        
        // Simple: Just send the price for the selected size
        cartData.put("product_price", String.valueOf((int)priceValue));
        cartData.put("product_mrp", String.valueOf((int)mrpValue)); 
        
        cartData.put("product_image", product.getProductImage() != null ? product.getProductImage() : "");
        cartData.put("quantity", quantity);
        
        if (selectedSize != null && !selectedSize.isEmpty()) {
            cartData.put("product_size", selectedSize);
        }

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
                    // Update ProductModel with selected size and price before adding to local manager
                    if (selectedSize != null && !selectedSize.isEmpty()) {
                        product.setSize(selectedSize);
                    }
                    if (dynamicPrice != null) product.setSellingPrice(dynamicPrice);
                    if (dynamicMrp != null) product.setMrp(dynamicMrp);
                    
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

