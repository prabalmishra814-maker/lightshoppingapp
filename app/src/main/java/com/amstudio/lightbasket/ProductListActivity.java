package com.amstudio.lightbasket;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.amstudio.lightbasket.api.SessionManager;
import com.amstudio.lightbasket.api.SupabaseClient;
import com.amstudio.lightbasket.models.ProductModel;
import com.amstudio.lightbasket.utils.StatusBarUtils;
import com.facebook.shimmer.ShimmerFrameLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends AppCompatActivity {

    private RecyclerView rvProducts;
    private ProductAdapter adapter;
    private List<ProductModel> productList = new ArrayList<>();
    private SessionManager sessionManager;
    private Set<String> cartProductIds = new HashSet<>();
    private Set<String> wishlistProductIds = new HashSet<>();

    private String categoryName;
    private String subCategoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.applyWhiteStatusBar(this);
        setContentView(R.layout.activity_product_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        String title = getIntent().getStringExtra("title");
        categoryName = getIntent().getStringExtra("category");
        subCategoryName = getIntent().getStringExtra("subcategory");
        
        if (title != null && getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        rvProducts = findViewById(R.id.rv_products);
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));

        sessionManager = new SessionManager(this);

        adapter = new ProductAdapter(this, productList, false, cartProductIds, wishlistProductIds, sessionManager, null);
        rvProducts.setAdapter(adapter);

        fetchCartProductIds();
        fetchWishlistProductIds();
        fetchProducts();
    }

    private void fetchProducts() {
        String authHeader = "Bearer " + SupabaseClient.SUPABASE_ANON_KEY;
        ShimmerFrameLayout shimmer = findViewById(R.id.shimmer_product_list);
        
        Call<List<ProductModel>> call;
        if (subCategoryName != null && !subCategoryName.isEmpty()) {
            Map<String, String> filters = new HashMap<>();
            filters.put("product_sub_category", "eq." + subCategoryName);
            call = SupabaseClient.getApiService().fetchProductsWithFilter(
                    SupabaseClient.SUPABASE_ANON_KEY,
                    authHeader,
                    "*",
                    filters
            );
        } else if (categoryName != null && !categoryName.isEmpty()) {
            Map<String, String> filters = new HashMap<>();
            filters.put("product_category", "eq." + categoryName);
            call = SupabaseClient.getApiService().fetchProductsWithFilter(
                    SupabaseClient.SUPABASE_ANON_KEY,
                    authHeader,
                    "*",
                    filters
            );
        } else {
            call = SupabaseClient.getApiService().fetchProducts(
                    SupabaseClient.SUPABASE_ANON_KEY,
                    authHeader,
                    "*"
            );
        }

        call.enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProductModel>> call, @NonNull Response<List<ProductModel>> response) {
                if (shimmer != null) {
                    shimmer.stopShimmer();
                    shimmer.setVisibility(View.GONE);
                }
                rvProducts.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProductModel>> call, @NonNull Throwable t) {
                if (shimmer != null) {
                    shimmer.stopShimmer();
                    shimmer.setVisibility(View.GONE);
                }
                Toast.makeText(ProductListActivity.this, "Something went wrong. Please contact the developer.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCartProductIds() {
        String userId = sessionManager.getUserId();
        if (userId.isEmpty()) return;

        String authHeader = "Bearer " + sessionManager.getToken();
        SupabaseClient.getApiService().fetchCart(
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "eq." + userId,
                "*"
        ).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(@NonNull Call<List<Map<String, Object>>> call, @NonNull Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cartProductIds.clear();
                    for (Map<String, Object> item : response.body()) {
                        Object pid = item.get("PRODUCT_ID") != null ? item.get("PRODUCT_ID") : item.get("product_id");
                        if (pid != null) cartProductIds.add(String.valueOf(pid));
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Map<String, Object>>> call, @NonNull Throwable t) {}
        });
    }

    private void fetchWishlistProductIds() {
        String userId = sessionManager.getUserId();
        if (userId.isEmpty()) return;

        String authHeader = "Bearer " + sessionManager.getToken();
        SupabaseClient.getApiService().fetchWishlist(
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "eq." + userId,
                "product_id"
        ).enqueue(new Callback<List<com.amstudio.lightbasket.models.WishlistModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<com.amstudio.lightbasket.models.WishlistModel>> call, @NonNull Response<List<com.amstudio.lightbasket.models.WishlistModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    wishlistProductIds.clear();
                    for (com.amstudio.lightbasket.models.WishlistModel item : response.body()) {
                        if (item.getProductId() != null) wishlistProductIds.add(String.valueOf(item.getProductId()));
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<com.amstudio.lightbasket.models.WishlistModel>> call, @NonNull Throwable t) {}
        });
    }
}

