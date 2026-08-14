package com.amstudio.lightbasket;

import android.content.Context;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.amstudio.lightbasket.api.SessionManager;
import com.amstudio.lightbasket.api.SupabaseClient;
import com.amstudio.lightbasket.models.ProductModel;
import com.amstudio.lightbasket.models.WishlistModel;
import com.amstudio.lightbasket.utils.StatusBarUtils;
import com.facebook.shimmer.ShimmerFrameLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private static final int SPEECH_REQUEST_CODE = 101;
    private EditText etSearch;
    private ImageView ivClear, ivMic;
    private TextView tvSearchTitle;
    private RecyclerView rvResults;
    private LinearLayout emptyState;
    private ProductAdapter adapter;
    private List<ProductModel> productList = new ArrayList<>();
    private List<ProductModel> suggestionList = new ArrayList<>();
    private SessionManager sessionManager;
    private Set<String> cartProductIds = new HashSet<>();
    private Set<String> wishlistProductIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.applyWhiteStatusBar(this);
        setContentView(R.layout.activity_search);

        initViews();
        setupRecyclerView();
        setupSearchLogic();
        
        fetchCartProductIds();
        fetchWishlistProductIds();
        fetchSuggestions();

        // Focus and open keyboard
        etSearch.postDelayed(() -> {
            etSearch.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 300); // Delay for transition to complete
    }

    private void initViews() {
        etSearch = findViewById(R.id.et_search);
        ImageView ivBack = findViewById(R.id.iv_back);
        ivClear = findViewById(R.id.iv_clear);
        ivMic = findViewById(R.id.iv_mic);
        tvSearchTitle = findViewById(R.id.tv_search_title);
        rvResults = findViewById(R.id.rv_search_results);
        emptyState = findViewById(R.id.empty_state);

        ivBack.setOnClickListener(v -> onBackPressed());
        ivClear.setOnClickListener(v -> etSearch.setText(""));
        
        ivMic.setOnClickListener(v -> startVoiceSearch());
    }

    private void startVoiceSearch() {
        android.content.Intent intent = new android.content.Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, java.util.Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to search...");
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(this, "Voice search not supported on your device", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                etSearch.setText(result.get(0));
            }
        }
    }

    private void setupRecyclerView() {
        rvResults.setLayoutManager(new GridLayoutManager(this, 2));
        sessionManager = new SessionManager(this);
        adapter = new ProductAdapter(this, productList, false, cartProductIds, wishlistProductIds, sessionManager, null);
        rvResults.setAdapter(adapter);
    }

    private void setupSearchLogic() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    ivClear.setVisibility(View.GONE);
                    showSuggestions();
                } else {
                    ivClear.setVisibility(View.VISIBLE);
                    emptyState.setVisibility(View.GONE);
                    tvSearchTitle.setVisibility(View.VISIBLE);
                    tvSearchTitle.setText("Search Results");
                    searchProducts(query);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchSuggestions() {
        String authHeader = "Bearer " + SupabaseClient.SUPABASE_ANON_KEY;
        ShimmerFrameLayout shimmer = findViewById(R.id.shimmer_search);
        
        if (shimmer != null) {
            shimmer.setVisibility(View.VISIBLE);
            shimmer.startShimmer();
        }
        rvResults.setVisibility(View.GONE);

        SupabaseClient.getApiService().fetchProducts(
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "*"
        ).enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProductModel>> call, @NonNull Response<List<ProductModel>> response) {
                if (shimmer != null) {
                    shimmer.stopShimmer();
                    shimmer.setVisibility(View.GONE);
                }
                rvResults.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null) {
                    suggestionList.clear();
                    suggestionList.addAll(response.body());
                    if (etSearch.getText().toString().trim().isEmpty()) {
                        showSuggestions();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<ProductModel>> call, @NonNull Throwable t) {
                if (shimmer != null) {
                    shimmer.stopShimmer();
                    shimmer.setVisibility(View.GONE);
                }
                rvResults.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showSuggestions() {
        productList.clear();
        productList.addAll(suggestionList);
        adapter.notifyDataSetChanged();
        if (productList.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            tvSearchTitle.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            tvSearchTitle.setVisibility(View.VISIBLE);
            tvSearchTitle.setText("Recommended for You");
        }
    }

    private void searchProducts(String query) {
        String authHeader = "Bearer " + SupabaseClient.SUPABASE_ANON_KEY;
        ShimmerFrameLayout shimmer = findViewById(R.id.shimmer_search);
        
        if (shimmer != null) {
            shimmer.setVisibility(View.VISIBLE);
            shimmer.startShimmer();
        }
        rvResults.setVisibility(View.GONE);
        
        SupabaseClient.getApiService().searchProducts(
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "*",
                "ilike.*" + query + "*"
        ).enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProductModel>> call, @NonNull Response<List<ProductModel>> response) {
                if (shimmer != null) {
                    shimmer.stopShimmer();
                    shimmer.setVisibility(View.GONE);
                }
                rvResults.setVisibility(View.VISIBLE);
                
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    
                    if (productList.isEmpty()) {
                        emptyState.setVisibility(View.VISIBLE);
                        tvSearchTitle.setVisibility(View.GONE);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<ProductModel>> call, @NonNull Throwable t) {
                if (shimmer != null) {
                    shimmer.stopShimmer();
                    shimmer.setVisibility(View.GONE);
                }
                rvResults.setVisibility(View.VISIBLE);
            }
        });
    }

    private void fetchCartProductIds() {
        String userId = sessionManager.getUserId();
        if (userId.isEmpty()) return;
        String authHeader = "Bearer " + sessionManager.getToken();
        SupabaseClient.getApiService().fetchCart(SupabaseClient.SUPABASE_ANON_KEY, authHeader, "eq." + userId, "*")
                .enqueue(new Callback<List<Map<String, Object>>>() {
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
        SupabaseClient.getApiService().fetchWishlist(SupabaseClient.SUPABASE_ANON_KEY, authHeader, "eq." + userId, "product_id")
                .enqueue(new Callback<List<WishlistModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<WishlistModel>> call, @NonNull Response<List<WishlistModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    wishlistProductIds.clear();
                    for (WishlistModel item : response.body()) {
                        if (item.getProductId() != null) wishlistProductIds.add(String.valueOf(item.getProductId()));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<WishlistModel>> call, @NonNull Throwable t) {}
        });
    }
}

