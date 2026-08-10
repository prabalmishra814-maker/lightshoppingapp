package com.example.lightshop;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.lightshop.api.SupabaseClient;
import com.example.lightshop.api.SessionManager;
import com.example.lightshop.databinding.ActivityProductDetailBinding;
import com.example.lightshop.models.ProductModel;
import com.example.lightshop.utils.StatusBarUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private ActivityProductDetailBinding binding;
    private ProductModel product;
    private int quantity = 1;
    private SessionManager sessionManager;
    private boolean isWishlisted = false;
    private java.util.Set<String> wishlistProductIds = new java.util.HashSet<>();
    private RecommendationAdapter youMayLikeAdapter, similarAdapter, recentlyViewedAdapter, moreAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.applyWhiteStatusBar(this);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        product = (ProductModel) getIntent().getSerializableExtra("product");

        if (product != null) {
            setupUI();
            checkWishlistStatus();
            fetchWishlistProductIds();
            setupRecommendations();
            sessionManager.addToRecentlyViewed(product);
            updateCartBadge();
        } else {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupClickListeners();
    }

    private void setupUI() {
        binding.tvProductTitle.setText(product.getProductName());
        binding.tvProductDescription.setText(product.getDescription() != null ? product.getDescription() : product.getShortDescription());
        
        String sellingPriceStr = product.getSellingPrice() != null ? product.getSellingPrice() : product.getPrice();
        String mrpStr = product.getMrp() != null ? product.getMrp() : product.getMainPrice();

        try {
            String sClean = sellingPriceStr != null ? sellingPriceStr.replaceAll("[^0-9.]", "") : "0";
            String mClean = mrpStr != null ? mrpStr.replaceAll("[^0-9.]", "") : "0";

            double sPrice = Double.parseDouble(sClean);
            double mPrice = Double.parseDouble(mClean);

            binding.tvPrice.setText("₹" + (int) sPrice);
            binding.tvMrp.setText("₹" + (int) mPrice);
            binding.tvMrp.setPaintFlags(binding.tvMrp.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);

            if (mPrice > sPrice && mPrice > 0) {
                int discount = (int) Math.round(((mPrice - sPrice) / mPrice) * 100);
                binding.tvDiscountPercent.setText(discount + "% off");
            }
        } catch (Exception e) {
            binding.tvPrice.setText("₹" + sellingPriceStr);
            binding.tvMrp.setText("₹" + mrpStr);
        }

        binding.tvRatingBadge.setText(product.getRating() + " ★");
        binding.tvReviewCount.setText("(" + (product.getReviewsCount() != null ? product.getReviewsCount() : "0") + ")");
        binding.tvSoldCount.setText((product.getSoldCount() != null ? product.getSoldCount() : "0") + " Sold");
        binding.tvStockStatus.setText(product.getStock() != null ? product.getStock() : "In Stock");

        setupImageSlider();
    }

    private void setupImageSlider() {
        List<SlideModel> slideModels = new ArrayList<>();
        
        if (product.getProductImage() != null && !product.getProductImage().isEmpty()) {
            slideModels.add(new SlideModel(product.getProductImage(), ScaleTypes.FIT));
        }
        if (product.getProductImage2() != null && !product.getProductImage2().isEmpty()) {
            slideModels.add(new SlideModel(product.getProductImage2(), ScaleTypes.FIT));
        }
        if (product.getProductImage3() != null && !product.getProductImage3().isEmpty()) {
            slideModels.add(new SlideModel(product.getProductImage3(), ScaleTypes.FIT));
        }
        if (product.getProductImage4() != null && !product.getProductImage4().isEmpty()) {
            slideModels.add(new SlideModel(product.getProductImage4(), ScaleTypes.FIT));
        }
        if (product.getProductImage5() != null && !product.getProductImage5().isEmpty()) {
            slideModels.add(new SlideModel(product.getProductImage5(), ScaleTypes.FIT));
        }

        if (slideModels.isEmpty()) {
            slideModels.add(new SlideModel(R.drawable.ic_headphones, ScaleTypes.FIT));
        }

        binding.imageSlider.setImageList(slideModels, ScaleTypes.FIT);

        binding.imageSlider.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemSelected(int position) {
                ArrayList<String> images = new ArrayList<>();
                if (product.getProductImage() != null && !product.getProductImage().isEmpty()) images.add(product.getProductImage());
                if (product.getProductImage2() != null && !product.getProductImage2().isEmpty()) images.add(product.getProductImage2());
                if (product.getProductImage3() != null && !product.getProductImage3().isEmpty()) images.add(product.getProductImage3());
                if (product.getProductImage4() != null && !product.getProductImage4().isEmpty()) images.add(product.getProductImage4());
                if (product.getProductImage5() != null && !product.getProductImage5().isEmpty()) images.add(product.getProductImage5());

                if (images.isEmpty()) {
                    // Fallback to placeholder if needed, but usually slider has at least one
                    return;
                }

                android.content.Intent intent = new android.content.Intent(ProductDetailActivity.this, FullScreenImageActivity.class);
                intent.putStringArrayListExtra("images", images);
                intent.putExtra("position", position);
                startActivity(intent);
            }
            
            @Override
            public void doubleClick(int position) {
                // Not used
            }
        });
    }

    private void setupRecommendations() {
        binding.rvYouMayAlsoLike.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
        binding.rvSimilarProducts.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
        binding.rvRecentlyViewed.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
        binding.rvMoreProducts.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));

        fetchRecommendations();
        loadRecentlyViewed();
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
        ).enqueue(new Callback<List<com.example.lightshop.models.WishlistModel>>() {
            @Override
            public void onResponse(Call<List<com.example.lightshop.models.WishlistModel>> call, Response<List<com.example.lightshop.models.WishlistModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (com.example.lightshop.models.WishlistModel item : response.body()) {
                        if (item.getProductId() != null) {
                            wishlistProductIds.add(String.valueOf(item.getProductId()));
                        }
                    }
                    if (youMayLikeAdapter != null) youMayLikeAdapter.notifyDataSetChanged();
                    if (similarAdapter != null) similarAdapter.notifyDataSetChanged();
                    if (recentlyViewedAdapter != null) recentlyViewedAdapter.notifyDataSetChanged();
                    if (moreAdapter != null) moreAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<List<com.example.lightshop.models.WishlistModel>> call, Throwable t) {}
        });
    }

    private void fetchRecommendations() {
        String authHeader = "Bearer " + SupabaseClient.SUPABASE_ANON_KEY;
        SupabaseClient.getApiService().fetchProducts(
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "*"
        ).enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(Call<List<ProductModel>> call, Response<List<ProductModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductModel> products = response.body();
                    
                    int size = products.size();
                    if (size > 0) {
                        youMayLikeAdapter = new RecommendationAdapter(ProductDetailActivity.this, products.subList(0, Math.min(size, 8)), wishlistProductIds);
                        binding.rvYouMayAlsoLike.setAdapter(youMayLikeAdapter);
                        
                        if (size > 4) {
                            similarAdapter = new RecommendationAdapter(ProductDetailActivity.this, products.subList(Math.min(4, size), Math.min(size, 12)), wishlistProductIds);
                            binding.rvSimilarProducts.setAdapter(similarAdapter);
                        }
                        
                        moreAdapter = new RecommendationAdapter(ProductDetailActivity.this, products, wishlistProductIds);
                        binding.rvMoreProducts.setAdapter(moreAdapter);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<ProductModel>> call, Throwable t) {}
        });
    }

    private void loadRecentlyViewed() {
        List<ProductModel> recent = sessionManager.getRecentlyViewed();
        if (!recent.isEmpty()) {
            recentlyViewedAdapter = new RecommendationAdapter(this, recent, wishlistProductIds);
            binding.rvRecentlyViewed.setAdapter(recentlyViewedAdapter);
        }
    }

    private void updateCartBadge() {
        String userId = sessionManager.getUserId();
        if (userId.isEmpty()) {
            binding.tvCartBadge.setVisibility(View.GONE);
            return;
        }

        String authHeader = "Bearer " + sessionManager.getToken();
        SupabaseClient.getApiService().fetchCart(
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "eq." + userId,
                "product_id"
        ).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int count = response.body().size();
                    if (count > 0) {
                        binding.tvCartBadge.setText(String.valueOf(count));
                        binding.tvCartBadge.setVisibility(View.VISIBLE);
                    } else {
                        binding.tvCartBadge.setVisibility(View.GONE);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {}
        });
    }

    private void setupClickListeners() {
        binding.ivBack.setOnClickListener(v -> onBackPressed());

        binding.btnPlus.setOnClickListener(v -> {
            quantity++;
            binding.tvQuantity.setText(String.valueOf(quantity));
        });

        binding.btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                binding.tvQuantity.setText(String.valueOf(quantity));
            }
        });

        binding.btnAddToCart.setOnClickListener(v -> addToCart());
        binding.btnWishlist.setOnClickListener(v -> toggleWishlist());
        binding.ivHeaderWishlist.setOnClickListener(v -> toggleWishlist());
        
        binding.btnBuyNow.setOnClickListener(v -> {
            // Navigate to CheckoutActivity
            android.content.Intent intent = new android.content.Intent(this, CheckoutActivity.class);
            startActivity(intent);
        });
    }

    private void addToCart() {
        String userId = sessionManager.getUserId();
        if (userId.isEmpty()) {
            Toast.makeText(this, "Please login to add to cart", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("user_id", userId);
        data.put("product_id", product.getProductId());
        data.put("product_name", product.getProductName());
        data.put("product_price", product.getSellingPrice() != null ? product.getSellingPrice() : product.getPrice());
        data.put("product_mrp", product.getMrp() != null ? product.getMrp() : (product.getMainPrice() != null ? product.getMainPrice() : product.getPrice()));
        data.put("product_image", product.getProductImage());
        data.put("quantity", quantity);

        String authHeader = "Bearer " + sessionManager.getToken();

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
                    Toast.makeText(ProductDetailActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                    updateCartBadge();
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Map<String, Object>>> call, @NonNull Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkWishlistStatus() {
        String userId = sessionManager.getUserId();
        if (userId.isEmpty()) return;

        String authHeader = "Bearer " + sessionManager.getToken();
        SupabaseClient.getApiService().fetchWishlist(
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "eq." + userId,
                "product_id"
        ).enqueue(new Callback<List<com.example.lightshop.models.WishlistModel>>() {
            @Override
            public void onResponse(Call<List<com.example.lightshop.models.WishlistModel>> call, Response<List<com.example.lightshop.models.WishlistModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (com.example.lightshop.models.WishlistModel item : response.body()) {
                        if (String.valueOf(item.getProductId()).equals(product.getProductId())) {
                            isWishlisted = true;
                            updateWishlistUI();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<com.example.lightshop.models.WishlistModel>> call, Throwable t) {
                android.util.Log.e("SupabaseError", "Wishlist Status Check Failed", t);
            }
        });
    }

    private void toggleWishlist() {
        if (isWishlisted) {
            removeFromWishlist();
        } else {
            addToWishlist();
        }
    }

    private void addToWishlist() {
        String userId = sessionManager.getUserId();
        if (userId.isEmpty()) return;

        Map<String, Object> data = new HashMap<>();
        data.put("user_id", userId);
        data.put("product_id", product.getProductId());
        data.put("product_name", product.getProductName());
        data.put("product_price", product.getSellingPrice() != null ? product.getSellingPrice() : product.getPrice());
        data.put("product_image", product.getProductImage());

        String authHeader = "Bearer " + sessionManager.getToken();
        SupabaseClient.getApiService().addData(
                "wishlist",
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "return=representation",
                data
        ).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(@NonNull Call<List<Map<String, Object>>> call, @NonNull Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful()) {
                    isWishlisted = true;
                    updateWishlistUI();
                    Toast.makeText(ProductDetailActivity.this, "Added to Wishlist", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Map<String, Object>>> call, @NonNull Throwable t) {}
        });
    }

    private void removeFromWishlist() {
        String userId = sessionManager.getUserId();
        if (userId.isEmpty()) return;

        String authHeader = "Bearer " + sessionManager.getToken();
        Map<String, String> filters = new HashMap<>();
        filters.put("user_id", "eq." + userId);
        filters.put("product_id", "eq." + product.getProductId());

        SupabaseClient.getApiService().deleteDataByFilters(
                "wishlist",
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                filters
            ).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    isWishlisted = false;
                    updateWishlistUI();
                    Toast.makeText(ProductDetailActivity.this, "Removed from Wishlist", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {}
        });
    }

    private void updateWishlistUI() {
        if (isWishlisted) {
            binding.ivHeaderWishlist.setImageResource(R.drawable.ic_heart_filled);
            binding.ivHeaderWishlist.setColorFilter(getResources().getColor(R.color.color_logout));
            binding.btnWishlist.setIconResource(R.drawable.ic_heart_filled);
            binding.btnWishlist.setIconTintResource(R.color.color_logout);
        } else {
            binding.ivHeaderWishlist.setImageResource(R.drawable.ic_heart_outline);
            binding.ivHeaderWishlist.setColorFilter(getResources().getColor(R.color.dark_navy));
            binding.btnWishlist.setIconResource(R.drawable.ic_heart_outline);
            binding.btnWishlist.setIconTintResource(R.color.primary_blue);
        }
    }
}
