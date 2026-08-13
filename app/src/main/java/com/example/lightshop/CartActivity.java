package com.example.lightshop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lightshop.utils.CartManager;
import com.example.lightshop.utils.PriceUtils;
import com.example.lightshop.utils.StatusBarUtils;

import android.widget.Toast;
import com.example.lightshop.api.SessionManager;
import com.example.lightshop.api.SupabaseClient;
import com.example.lightshop.models.CartItem;
import com.example.lightshop.models.ProductModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartUpdateListener {

    private RecyclerView rvCartItems;
    private CartAdapter adapter;
    private TextView tvCartTitle, tvItemsCount, tvTotalPriceMrp, tvTotalDiscount, tvTotalSellingPrice, tvSavingsMessage;
    private Button btnPlaceOrder;
    private SessionManager sessionManager;
    private List<CartItem> cartItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.applyWhiteStatusBar(this);
        setContentView(R.layout.activity_cart);

        sessionManager = new SessionManager(this);
        initViews();
        setupRecyclerView();
        fetchCartItems();
        fetchUserAddress();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnChangeAddress).setOnClickListener(v -> {
            startActivity(new Intent(this, AddAddressActivity.class));
        });

        btnPlaceOrder.setOnClickListener(v -> {
            startActivity(new Intent(this, CheckoutActivity.class));
        });
    }

    private void fetchCartItems() {
        String userId = sessionManager.getUserId();
        if (userId.isEmpty()) {
            Toast.makeText(this, "Please login to view cart", Toast.LENGTH_SHORT).show();
            return;
        }

        String userToken = sessionManager.getToken();
        String authHeader = "Bearer " + (userToken != null ? userToken : SupabaseClient.SUPABASE_ANON_KEY);

        SupabaseClient.getApiService().fetchCart(
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "eq." + userId,
                "*"
        ).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CartItem> fetchedItems = new ArrayList<>();
                    if (response.body().isEmpty()) {
                        // Cart is empty
                    }
                    
                    for (Map<String, Object> map : response.body()) {
                        try {
                            ProductModel p = new ProductModel();
                            // Mapping correct columns from DB to ProductModel
                            p.setProductId(getStringFromMap(map, "product_id", "PRODUCT_ID"));
                            p.setProductName(getStringFromMap(map, "product_name"));
                            
                            String sellingPrice = getStringFromMap(map, "product_price", "product_selling_price");
                            p.setSellingPrice(sellingPrice);
                            p.setProductImage(getStringFromMap(map, "product_image"));
                            
                            String mrp = getStringFromMap(map, "product_mrp");
                            p.setMrp(mrp != null && !mrp.isEmpty() ? mrp : sellingPrice);

                            int qty = 1;
                            Object qtyObj = map.get("quantity");
                            if (qtyObj instanceof Number) {
                                qty = ((Number) qtyObj).intValue();
                            } else if (qtyObj instanceof String) {
                                try {
                                    qty = Integer.parseInt((String) qtyObj);
                                } catch (NumberFormatException ignored) {}
                            }
                            
                            fetchedItems.add(new CartItem(p, qty));
                        } catch (Exception e) {
                            android.util.Log.e("CartActivity", "Error parsing item", e);
                        }
                    }

                    // Reverse the list to show latest items at the top
                    java.util.Collections.reverse(fetchedItems);
                    
                    // Sync with Manager
                    CartManager.getInstance().setCartItems(fetchedItems);
                    
                    // Update Activity's local reference and notify
                    cartItems.clear();
                    cartItems.addAll(fetchedItems);
                    adapter.notifyDataSetChanged();
                    updatePriceDetails();
                } else {
                    String error = "Failed to load cart: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            error += " - " + response.errorBody().string();
                        }
                    } catch (Exception ignored) {}
                    Toast.makeText(CartActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        rvCartItems = findViewById(R.id.rvCartItems);
        tvCartTitle = findViewById(R.id.tvCartTitle);
        tvItemsCount = findViewById(R.id.tvItemsCount);
        tvTotalPriceMrp = findViewById(R.id.tvTotalPriceMrp);
        tvTotalDiscount = findViewById(R.id.tvTotalDiscount);
        tvTotalSellingPrice = findViewById(R.id.tvTotalSellingPrice);
        tvSavingsMessage = findViewById(R.id.tvSavingsMessage);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
    }

    private void setupRecyclerView() {
        adapter = new CartAdapter(cartItems, this); // Use the local list directly
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        rvCartItems.setAdapter(adapter);
    }

    private void updatePriceDetails() {
        CartManager manager = CartManager.getInstance();
        int count = manager.getCartItems().size();
        
        tvCartTitle.setText("My Cart (" + count + ")");
        tvItemsCount.setText("Price (" + count + " items)");
        tvTotalPriceMrp.setText(PriceUtils.formatPrice(manager.getTotalMrp()));
        tvTotalDiscount.setText("-" + PriceUtils.formatPrice(manager.getTotalDiscount()));
        tvTotalSellingPrice.setText(PriceUtils.formatPrice(manager.getTotalSellingPrice()));
        tvSavingsMessage.setText("You will save " + PriceUtils.formatPrice(manager.getTotalDiscount()) + " on this order");
        
        btnPlaceOrder.setEnabled(count > 0);
    }

    @Override
    public void onQuantityChanged(int position, int delta) {
        if (position < 0 || position >= cartItems.size()) return;

        CartItem item = cartItems.get(position);
        int oldQty = item.getQuantity();
        int newQty = oldQty + delta;

        if (newQty <= 0) {
            onRemoveItem(position);
            return;
        }

        // Optimistic Update
        item.setQuantity(newQty);
        CartManager.getInstance().updateQuantityByProductId(item.getProduct().getProductId(), newQty);
        adapter.notifyItemChanged(position);
        updatePriceDetails();

        String userId = sessionManager.getUserId();
        String authHeader = "Bearer " + (sessionManager.getToken() != null ? sessionManager.getToken() : SupabaseClient.SUPABASE_ANON_KEY);

        Map<String, Object> updateData = new java.util.HashMap<>();
        updateData.put("quantity", newQty);

        java.util.Map<String, String> filters = new java.util.HashMap<>();
        filters.put("user_id", "eq." + userId);
        filters.put("product_id", "eq." + item.getProduct().getProductId());

        SupabaseClient.getApiService().updateDataByColumn(
                "cart",
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                filters,
                updateData
        ).enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                if (!response.isSuccessful()) {
                    // Rollback
                    item.setQuantity(oldQty);
                    CartManager.getInstance().updateQuantityByProductId(item.getProduct().getProductId(), oldQty);
                    adapter.notifyItemChanged(position);
                    updatePriceDetails();
                    Toast.makeText(CartActivity.this, "Failed to update quantity", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                // Rollback
                item.setQuantity(oldQty);
                CartManager.getInstance().updateQuantityByProductId(item.getProduct().getProductId(), oldQty);
                adapter.notifyItemChanged(position);
                updatePriceDetails();
                Toast.makeText(CartActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRemoveItem(int position) {
        if (position < 0 || position >= cartItems.size()) return;

        CartItem item = cartItems.get(position);
        String userId = sessionManager.getUserId();
        String authHeader = "Bearer " + sessionManager.getToken();

        java.util.Map<String, String> filters = new java.util.HashMap<>();
        filters.put("user_id", "eq." + userId);
        filters.put("product_id", "eq." + item.getProduct().getProductId());

        SupabaseClient.getApiService().deleteDataByFilters(
                "cart",
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                filters
        ).enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Remove from local list to sync with UI
                    cartItems.remove(position);
                    CartManager.getInstance().removeItem(position);
                    
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, cartItems.size());
                    updatePriceDetails();
                }
            }

            @Override
            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Remove failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMoveToWishlist(int position) {
        if (position < 0 || position >= cartItems.size()) return;

        CartItem item = cartItems.get(position);
        ProductModel product = item.getProduct();
        String userId = sessionManager.getUserId();
        String userToken = sessionManager.getToken();
        String authHeader = "Bearer " + (userToken != null ? userToken : SupabaseClient.SUPABASE_ANON_KEY);

        // 1. Add to Wishlist
        Map<String, Object> wishlistData = new java.util.HashMap<>();
        wishlistData.put("user_id", userId);
        wishlistData.put("product_id", product.getProductId());
        wishlistData.put("product_name", product.getProductName());
        wishlistData.put("product_price", product.getSellingPrice());
        wishlistData.put("product_image", product.getProductImage());

        SupabaseClient.getApiService().addData(
                "wishlist",
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "return=representation",
                wishlistData
        ).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful()) {
                    // 2. Remove from Cart
                    onRemoveItem(position);
                    Toast.makeText(CartActivity.this, "Moved to Wishlist", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CartActivity.this, "Failed to move to wishlist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchUserAddress();
    }

    private void fetchUserAddress() {
        String userId = sessionManager.getUserId();
        if (userId == null || userId.isEmpty()) return;

        String userToken = sessionManager.getToken();
        String authHeader = "Bearer " + (userToken != null ? userToken : SupabaseClient.SUPABASE_ANON_KEY);
        Map<String, String> filters = new java.util.HashMap<>();
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

    private void moveAllToWishlist() {
        String userId = sessionManager.getUserId();
        String userToken = sessionManager.getToken();
        String authHeader = "Bearer " + (userToken != null ? userToken : SupabaseClient.SUPABASE_ANON_KEY);
        
        List<Map<String, Object>> wishlistItems = new ArrayList<>();
        for (CartItem item : cartItems) {
            Map<String, Object> data = new HashMap<>();
            data.put("user_id", userId);
            data.put("product_id", item.getProduct().getProductId());
            data.put("product_name", item.getProduct().getProductName());
            data.put("product_price", item.getProduct().getSellingPrice());
            data.put("product_image", item.getProduct().getProductImage());
            wishlistItems.add(data);
        }

        // 1. Add all to Wishlist
        SupabaseClient.getApiService().addData(
                "wishlist",
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "return=representation",
                wishlistItems
        ).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful()) {
                    // 2. Clear Cart in DB
                    clearCartFromDb(userId, authHeader);
                } else {
                    Toast.makeText(CartActivity.this, "Failed to move items", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearCartFromDb(String userId, String authHeader) {
        Map<String, String> filters = new HashMap<>();
        filters.put("user_id", "eq." + userId);

        SupabaseClient.getApiService().deleteDataByFilters(
                "cart",
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                filters
        ).enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                if (response.isSuccessful()) {
                    cartItems.clear();
                    CartManager.getInstance().getCartItems().clear();
                    adapter.notifyDataSetChanged();
                    updatePriceDetails();
                    Toast.makeText(CartActivity.this, "All items moved to wishlist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Cart clear failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAddressUI(Map<String, Object> address) {
        try {
            String city = (String) address.get("city");
            String pincode = (String) address.get("pincode");
            if (city != null && pincode != null) {
                ((TextView) findViewById(R.id.tvDeliverTo)).setText(city + " " + pincode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getStringFromMap(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            Object value = map.get(key);
            if (value != null) {
                String strValue = String.valueOf(value);
                if (!strValue.equalsIgnoreCase("null") && !strValue.isEmpty()) {
                    return strValue;
                }
            }
        }
        return "0";
    }
}
