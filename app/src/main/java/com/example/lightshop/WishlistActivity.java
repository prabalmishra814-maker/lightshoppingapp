package com.example.lightshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lightshop.models.ProductModel;
import com.example.lightshop.models.WishlistModel;
import com.example.lightshop.api.SupabaseClient;
import com.example.lightshop.api.SessionManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WishlistActivity extends AppCompatActivity implements WishlistAdapter.OnWishlistInteractionListener {

    private RecyclerView rvWishlist;
    private View layoutEmpty;
    private WishlistAdapter adapter;
    private List<ProductModel> wishlistList = new ArrayList<>();
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        sessionManager = new SessionManager(this);
        initViews();
        setupRecyclerView();
        fetchWishlist();
    }

    private void initViews() {
        rvWishlist = findViewById(R.id.rvWishlist);
        layoutEmpty = findViewById(R.id.layoutEmpty);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnCart).setOnClickListener(v -> {
            startActivity(new Intent(this, CartActivity.class));
        });
        findViewById(R.id.btnContinueShopping).setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new WishlistAdapter(wishlistList, this);
        rvWishlist.setLayoutManager(new GridLayoutManager(this, 2));
        rvWishlist.setAdapter(adapter);
    }

    private void fetchWishlist() {
        String userId = sessionManager.getUserId();
        if (userId.isEmpty()) {
            updateUI();
            return;
        }

        String userToken = sessionManager.getToken();
        String authHeader = "Bearer " + (userToken != null && !userToken.isEmpty() ? userToken : SupabaseClient.SUPABASE_ANON_KEY);
        
        SupabaseClient.getApiService().fetchWishlist(
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "eq." + userId,
                "*"
        ).enqueue(new Callback<List<WishlistModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<WishlistModel>> call, @NonNull Response<List<WishlistModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    wishlistList.clear();
                    for (WishlistModel item : response.body()) {
                        ProductModel p = new ProductModel();
                        p.setProductId(item.getProductId());
                        p.setProductName(item.getProductName());
                        p.setSellingPrice(item.getProductPrice());
                        p.setMrp(item.getProductPrice()); // Fallback as wishlist table lacks MRP
                        p.setProductImage(item.getProductImage());
                        wishlistList.add(p);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    // Show the actual error code (e.g., 401 or 404) to help debugging
                    String errorMsg = "Error: " + response.code();
                    if (response.code() == 404) {
                        errorMsg = "Wishlist table not found. Please check database.";
                    } else if (response.code() == 401) {
                        errorMsg = "Session expired. Please log in again.";
                    }
                    Toast.makeText(WishlistActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
                updateUI();
            }

            @Override
            public void onFailure(@NonNull Call<List<WishlistModel>> call, @NonNull Throwable t) {
                updateUI();
                Toast.makeText(WishlistActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        if (wishlistList.isEmpty()) {
            rvWishlist.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvWishlist.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRemoveItem(int position) {
        ProductModel product = wishlistList.get(position);
        String userId = sessionManager.getUserId();

        Map<String, String> filters = new HashMap<>();
        filters.put("user_id", "eq." + userId);
        filters.put("product_id", "eq." + product.getProductId());

        String userToken = sessionManager.getToken();
        String authHeader = "Bearer " + (userToken != null && !userToken.isEmpty() ? userToken : SupabaseClient.SUPABASE_ANON_KEY);
        
        SupabaseClient.getApiService().deleteDataByFilters(
                "wishlist",
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                filters
        ).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    wishlistList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, wishlistList.size());
                    updateUI();
                    Toast.makeText(WishlistActivity.this, "Removed from wishlist", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WishlistActivity.this, "Failed to remove", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(WishlistActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAddToCart(int position) {
        if (position < 0 || position >= wishlistList.size()) return;
        
        ProductModel product = wishlistList.get(position);
        String userId = sessionManager.getUserId();
        if (userId.isEmpty()) {
            Toast.makeText(this, "Please login to add to cart", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("user_id", userId);
        data.put("product_id", product.getProductId());
        data.put("product_name", product.getProductName());
        data.put("product_price", product.getSellingPrice());
        data.put("product_mrp", product.getMrp() != null ? product.getMrp() : product.getSellingPrice());
        data.put("product_image", product.getProductImage());
        data.put("quantity", 1);

        String userToken = sessionManager.getToken();
        String authHeader = "Bearer " + (userToken != null && !userToken.isEmpty() ? userToken : SupabaseClient.SUPABASE_ANON_KEY);

        SupabaseClient.getApiService().addData(
                "cart",
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "resolution=merge-duplicates,return=representation",
                data
        ).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(@NonNull Call<List<Map<String, Object>>> call, @NonNull Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(WishlistActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WishlistActivity.this, "Failed to add to cart: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Map<String, Object>>> call, @NonNull Throwable t) {
                Toast.makeText(WishlistActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(ProductModel product) {
        // Navigate to details if needed
    }
}
