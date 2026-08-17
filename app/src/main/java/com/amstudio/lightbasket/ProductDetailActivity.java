package com.amstudio.lightbasket;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.amstudio.lightbasket.api.SupabaseClient;
import com.amstudio.lightbasket.api.SessionManager;
import com.amstudio.lightbasket.databinding.ActivityProductDetailBinding;
import com.amstudio.lightbasket.models.ProductModel;
import com.amstudio.lightbasket.models.ReviewModel;
import com.amstudio.lightbasket.utils.StatusBarUtils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

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
    private boolean isAlreadyInCart = false;
    private boolean isDescriptionExpanded = false;
    private String selectedSize = "";
    private String selectedPrice = ""; // Store dynamic price
    private String selectedMrp = "";   // Store dynamic MRP
    private java.util.Set<String> wishlistProductIds = new java.util.HashSet<>();
    private RecommendationAdapter youMayLikeAdapter, moreAdapter;
    private ReviewAdapter reviewAdapter;
    private List<ReviewModel> reviewList = new ArrayList<>();

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
            checkCartStatus();
            fetchWishlistProductIds();
            setupRecommendations();
            setupReviews();
            sessionManager.addToRecentlyViewed(product);
            updateCartBadge();
        } else {
            Toast.makeText(this, "Something went wrong. Please contact the developer.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupClickListeners();
    }

    private void setupUI() {
        binding.tvProductTitle.setText(product.getProductName() != null ? product.getProductName() : "Unknown Product");
        binding.tvProductDescription.setText(product.getDescription() != null ? product.getDescription() : (product.getShortDescription() != null ? product.getShortDescription() : "No description available"));
        
        // Initial setup with base price
        selectedPrice = product.getSellingPrice() != null ? product.getSellingPrice() : (product.getPrice() != null ? product.getPrice() : "0");
        selectedMrp = product.getMrp() != null ? product.getMrp() : (product.getMainPrice() != null ? product.getMainPrice() : selectedPrice);

        updatePriceDisplay(selectedPrice, selectedMrp);

        try {
            int stockCount = Integer.parseInt(product.getStock() != null ? product.getStock() : "0");
            if (stockCount <= 0) {
                binding.tvStockStatus.setText("Out of Stock");
                binding.tvStockStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                binding.btnAddToCart.setEnabled(false);
                binding.btnBuyNow.setEnabled(false);
            } else if (stockCount <= 10) {
                binding.tvStockStatus.setText("Only " + stockCount + " left in stock!");
                binding.tvStockStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            } else {
                binding.tvStockStatus.setText("In Stock");
                binding.tvStockStatus.setTextColor(getResources().getColor(R.color.success_green));
            }
        } catch (Exception e) {
            binding.tvStockStatus.setText(product.getStock() != null ? product.getStock() : "In Stock");
        }

        setupDescriptionExpand();
        setupImageSlider();
        setupDates();
        setupSizeSelection();
    }

    private void updatePriceDisplay(String priceStr, String mrpStr) {
        try {
            String sClean = priceStr != null ? priceStr.replaceAll("[^0-9.]", "") : "0";
            String mClean = mrpStr != null ? mrpStr.replaceAll("[^0-9.]", "") : "0";

            if (sClean.isEmpty()) sClean = "0";
            if (mClean.isEmpty()) mClean = sClean;

            double sPrice = Double.parseDouble(sClean);
            double mPrice = Double.parseDouble(mClean);

            binding.tvPrice.setText("₹" + (int) sPrice);
            binding.tvMrp.setText("₹" + (int) mPrice);
            binding.tvMrp.setPaintFlags(binding.tvMrp.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);

            if (mPrice > sPrice && mPrice > 0) {
                int discount = (int) Math.round(((mPrice - sPrice) / mPrice) * 100);
                binding.tvDiscountPercent.setText(discount + "% off");
                binding.tvDiscountPercent.setVisibility(View.VISIBLE);
            } else {
                binding.tvDiscountPercent.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            binding.tvPrice.setText("₹" + priceStr);
            binding.tvMrp.setText("₹" + mrpStr);
            binding.tvDiscountPercent.setVisibility(View.GONE);
        }
    }

    private void setupSizeSelection() {
        if (product.getSize() == null || product.getSize().isEmpty() || product.getSize().equals("NULL")) {
            binding.layoutSizeSelection.setVisibility(View.GONE);
            return;
        }

        List<ProductModel.SizeVariant> variants = product.getVariants();

        binding.layoutSizeSelection.setVisibility(View.VISIBLE);
        binding.cgSizes.removeAllViews();

        if (variants != null && !variants.isEmpty()) {
            // Logic for JSON Variants
            for (ProductModel.SizeVariant variant : variants) {
                addSizeChip(variant.sizeName, variant.variantPrice, variant.variantMrp);
            }
        } else {
            // Fallback for simple comma-separated sizes
            String[] sizes = product.getSize().split(",");
            for (String size : sizes) {
                addSizeChip(size.trim(), null, null);
            }
        }

        // Auto-select first size
        if (binding.cgSizes.getChildCount() > 0) {
            ((com.google.android.material.chip.Chip) binding.cgSizes.getChildAt(0)).setChecked(true);
        }
    }

    private void addSizeChip(String sizeName, String variantPrice, String variantMrp) {
        if (sizeName.isEmpty()) return;

        com.google.android.material.chip.Chip chip = new com.google.android.material.chip.Chip(this);
        chip.setText(sizeName);
        chip.setCheckable(true);
        chip.setClickable(true);
        
        chip.setChipBackgroundColorResource(android.R.color.white);
        chip.setChipStrokeColor(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.divider_color)));
        chip.setChipStrokeWidth(com.amstudio.lightbasket.utils.PriceUtils.dpToPx(this, 1));
        chip.setTextColor(getResources().getColor(R.color.text_primary));

        chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedSize = sizeName;
                
                // Update price if it's a variant
                if (variantPrice != null && !variantPrice.isEmpty()) {
                    selectedPrice = variantPrice;
                    selectedMrp = (variantMrp != null && !variantMrp.isEmpty()) ? variantMrp : variantPrice;
                    updatePriceDisplay(selectedPrice, selectedMrp);
                    checkCartStatus(); // Re-check cart status for this specific size
                }

                chip.setChipStrokeColor(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.primary_blue)));
                chip.setChipStrokeWidth(com.amstudio.lightbasket.utils.PriceUtils.dpToPx(this, 2));
                chip.setTextColor(getResources().getColor(R.color.primary_blue));
            } else {
                chip.setChipStrokeColor(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.divider_color)));
                chip.setChipStrokeWidth(com.amstudio.lightbasket.utils.PriceUtils.dpToPx(this, 1));
                chip.setTextColor(getResources().getColor(R.color.text_primary));
            }
        });

        binding.cgSizes.addView(chip);
    }

    private void setupDates() {
        boolean showMfg = product.getManufactureDate() != null && !product.getManufactureDate().isEmpty();
        boolean showExp = product.getExpiryDate() != null && !product.getExpiryDate().isEmpty();

        if (showMfg || showExp) {
            binding.cardProductDates.setVisibility(View.VISIBLE);
            
            if (showMfg) {
                binding.layoutMfgDate.setVisibility(View.VISIBLE);
                binding.tvMfgDate.setText(product.getManufactureDate());
            } else {
                binding.layoutMfgDate.setVisibility(View.GONE);
            }

            if (showExp) {
                binding.layoutExpDate.setVisibility(View.VISIBLE);
                binding.tvExpDate.setText(product.getExpiryDate());
            } else {
                binding.layoutExpDate.setVisibility(View.GONE);
            }

            if (showMfg && showExp) {
                binding.dateDivider.setVisibility(View.VISIBLE);
            } else {
                binding.dateDivider.setVisibility(View.GONE);
            }
        } else {
            binding.cardProductDates.setVisibility(View.GONE);
        }
    }

    private void setupDescriptionExpand() {
        binding.tvProductDescription.post(() -> {
            if (binding.tvProductDescription.getLineCount() > 3) {
                binding.tvReadMore.setVisibility(View.VISIBLE);
            } else {
                binding.tvReadMore.setVisibility(View.GONE);
            }
        });

        View.OnClickListener toggleDescription = v -> {
            if (isDescriptionExpanded) {
                binding.tvProductDescription.setMaxLines(3);
                binding.tvReadMore.setText("Read More");
            } else {
                binding.tvProductDescription.setMaxLines(Integer.MAX_VALUE);
                binding.tvReadMore.setText("Read Less");
            }
            isDescriptionExpanded = !isDescriptionExpanded;
        };

        binding.tvReadMore.setOnClickListener(toggleDescription);
        binding.tvProductDescription.setOnClickListener(toggleDescription);
    }

    private void setupImageSlider() {
        List<SlideModel> slideModels = new ArrayList<>();
        
        addImageToSlider(product.getProductImage(), slideModels);
        addImageToSlider(product.getProductImage2(), slideModels);
        addImageToSlider(product.getProductImage3(), slideModels);
        addImageToSlider(product.getProductImage4(), slideModels);
        addImageToSlider(product.getProductImage5(), slideModels);

        if (slideModels.isEmpty()) {
            slideModels.add(new SlideModel(R.drawable.ic_headphones, ScaleTypes.FIT));
        }

        binding.imageSlider.setImageList(slideModels, ScaleTypes.FIT);

        binding.imageSlider.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemSelected(int position) {
                ArrayList<String> images = new ArrayList<>();
                addImageToList(product.getProductImage(), images);
                addImageToList(product.getProductImage2(), images);
                addImageToList(product.getProductImage3(), images);
                addImageToList(product.getProductImage4(), images);
                addImageToList(product.getProductImage5(), images);

                if (images.isEmpty()) return;

                android.content.Intent intent = new android.content.Intent(ProductDetailActivity.this, FullScreenImageActivity.class);
                intent.putStringArrayListExtra("images", images);
                intent.putExtra("position", position);
                startActivity(intent);
            }
            
            @Override
            public void doubleClick(int position) {}
        });
    }

    private void addImageToSlider(String imageSource, List<SlideModel> slideModels) {
        if (imageSource == null || imageSource.isEmpty()) return;

        if (imageSource.startsWith("http")) {
            slideModels.add(new SlideModel(imageSource, ScaleTypes.FIT));
        } else {
            // Try to resolve as drawable resource name
            int resId = getResources().getIdentifier(imageSource, "drawable", getPackageName());
            if (resId != 0) {
                slideModels.add(new SlideModel(resId, ScaleTypes.FIT));
            } else if (imageSource.contains("/") || imageSource.contains(".")) {
                // It might be a URL without http or some other path, try as URL anyway
                slideModels.add(new SlideModel(imageSource, ScaleTypes.FIT));
            }
        }
    }

    private void addImageToList(String imageSource, List<String> images) {
        if (imageSource == null || imageSource.isEmpty()) return;
        images.add(imageSource);
    }

    private void setupRecommendations() {
        binding.rvYouMayAlsoLike.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
        binding.rvMoreProducts.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));

        fetchRecommendations();
    }

    private void setupReviews() {
        binding.rvReviews.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter(reviewList);
        binding.rvReviews.setAdapter(reviewAdapter);
        fetchReviews();
    }

    private void fetchReviews() {
        String productIdFilter = "eq." + product.getProductId();
        SupabaseClient.getApiService().fetchReviews(
                SupabaseClient.SUPABASE_ANON_KEY,
                "Bearer " + SupabaseClient.SUPABASE_ANON_KEY,
                productIdFilter,
                "*"
        ).enqueue(new Callback<List<ReviewModel>>() {
            @Override
            public void onResponse(Call<List<ReviewModel>> call, Response<List<ReviewModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    reviewList.clear();
                    reviewList.addAll(response.body());
                    reviewAdapter.notifyDataSetChanged();
                    calculateAverageRating(reviewList);
                }
            }

            @Override
            public void onFailure(Call<List<ReviewModel>> call, Throwable t) {
                android.util.Log.e("ReviewFetch", "Error: " + t.getMessage());
            }
        });
    }

    private void calculateAverageRating(List<ReviewModel> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            binding.tvAvgRatingTop.setText("0.0");
            binding.tvReviewsCountTop.setText("0 reviews");
            binding.tvAvgRatingLarge.setText("0.0");
            binding.avgRatingBar.setRating(0);
            binding.tvTotalReviewsCount.setText("Based on 0 reviews");
            return;
        }

        float totalRating = 0;
        for (ReviewModel review : reviews) {
            totalRating += review.getRating();
        }
        float average = totalRating / reviews.size();

        String avgStr = String.format("%.1f", average);
        binding.tvAvgRatingTop.setText(avgStr);
        binding.tvReviewsCountTop.setText(reviews.size() + " reviews");
        binding.tvAvgRatingLarge.setText(avgStr);
        binding.avgRatingBar.setRating(average);
        binding.tvTotalReviewsCount.setText("Based on " + reviews.size() + " reviews");
    }

    private void showAddReviewDialog() {
        if (sessionManager.getUserId().isEmpty()) {
            Toast.makeText(this, "Please login to write a review", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AuthActivity.class));
            return;
        }

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_review, null);
        dialog.setContentView(dialogView);

        android.widget.RatingBar ratingBar = dialogView.findViewById(R.id.dialog_rating_bar);
        TextInputEditText etComment = dialogView.findViewById(R.id.et_comment);
        com.google.android.material.button.MaterialButton btnSubmit = dialogView.findViewById(R.id.btn_submit_review);

        btnSubmit.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            if (rating == 0) {
                Toast.makeText(this, "Please select at least 1 star", Toast.LENGTH_SHORT).show();
                return;
            }

            String comment = etComment.getText().toString().trim();
            submitReviewToSupabase(rating, comment, dialog);
        });

        dialog.show();
    }

    private void submitReviewToSupabase(float rating, String comment, BottomSheetDialog dialog) {
        ReviewModel newReview = new ReviewModel();
        newReview.setProductId(product.getProductId());
        newReview.setUserId(sessionManager.getUserId());
        newReview.setUserName(sessionManager.getUserName() != null ? sessionManager.getUserName() : "User");
        newReview.setRating((int) rating);
        newReview.setComment(comment);

        SupabaseClient.getApiService().addData(
                "reviews",
                SupabaseClient.SUPABASE_ANON_KEY,
                "Bearer " + (sessionManager.getToken() != null ? sessionManager.getToken() : SupabaseClient.SUPABASE_ANON_KEY),
                "return=representation",
                newReview
        ).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProductDetailActivity.this, "Review submitted successfully!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    fetchReviews(); // Refresh the list
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Failed to submit review", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
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
            public void onResponse(Call<List<com.amstudio.lightbasket.models.WishlistModel>> call, Response<List<com.amstudio.lightbasket.models.WishlistModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (com.amstudio.lightbasket.models.WishlistModel item : response.body()) {
                        if (item.getProductId() != null) {
                            wishlistProductIds.add(String.valueOf(item.getProductId()));
                        }
                    }
                    if (youMayLikeAdapter != null) youMayLikeAdapter.notifyDataSetChanged();
                    if (moreAdapter != null) moreAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<List<com.amstudio.lightbasket.models.WishlistModel>> call, Throwable t) {}
        });
    }

    private void fetchRecommendations() {
        String authHeader = "Bearer " + SupabaseClient.SUPABASE_ANON_KEY;
        String subCat = product.getSubCategory();

        if (subCat != null && !subCat.isEmpty()) {
            Map<String, String> filters = new HashMap<>();
            filters.put("product_sub_category", "eq." + subCat);
            
            SupabaseClient.getApiService().fetchProductsWithFilter(
                    SupabaseClient.SUPABASE_ANON_KEY,
                    authHeader,
                    "*",
                    filters
            ).enqueue(new Callback<List<ProductModel>>() {
                @Override
                public void onResponse(@NonNull Call<List<ProductModel>> call, @NonNull Response<List<ProductModel>> response) {
                    if (binding.shimmerProductDetail != null) {
                        binding.shimmerProductDetail.stopShimmer();
                        binding.shimmerProductDetail.setVisibility(View.GONE);
                    }
                    binding.scrollViewContent.setVisibility(View.VISIBLE);

                    if (response.isSuccessful() && response.body() != null) {
                        List<ProductModel> products = new ArrayList<>(response.body());
                        // Remove current product from recommendations
                        for (int i = 0; i < products.size(); i++) {
                            if (products.get(i).getProductId().equals(product.getProductId())) {
                                products.remove(i);
                                break;
                            }
                        }
                        
                        if (!products.isEmpty()) {
                            youMayLikeAdapter = new RecommendationAdapter(ProductDetailActivity.this, products, wishlistProductIds);
                            binding.rvYouMayAlsoLike.setAdapter(youMayLikeAdapter);
                        }
                    }
                    // Fetch other generic recommendations if needed, or just stop here.
                    // For now, let's also fetch general products for "More Products"
                    fetchMoreProducts();
                }

                @Override
                public void onFailure(@NonNull Call<List<ProductModel>> call, @NonNull Throwable t) {
                    if (binding.shimmerProductDetail != null) {
                        binding.shimmerProductDetail.stopShimmer();
                        binding.shimmerProductDetail.setVisibility(View.GONE);
                    }
                    binding.scrollViewContent.setVisibility(View.VISIBLE);
                    fetchMoreProducts();
                }
            });
        } else {
            if (binding.shimmerProductDetail != null) {
                binding.shimmerProductDetail.stopShimmer();
                binding.shimmerProductDetail.setVisibility(View.GONE);
            }
            binding.scrollViewContent.setVisibility(View.VISIBLE);
            fetchMoreProducts();
        }
    }

    private void fetchMoreProducts() {
        String authHeader = "Bearer " + SupabaseClient.SUPABASE_ANON_KEY;
        SupabaseClient.getApiService().fetchProducts(
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "*"
        ).enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProductModel>> call, @NonNull Response<List<ProductModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductModel> products = response.body();
                    int size = products.size();
                    
                    if (size > 0) {
                        // If youMayLikeAdapter is still null (maybe subCat was empty or fetch failed)
                        if (youMayLikeAdapter == null) {
                            youMayLikeAdapter = new RecommendationAdapter(ProductDetailActivity.this, products.subList(0, Math.min(size, 8)), wishlistProductIds);
                            binding.rvYouMayAlsoLike.setAdapter(youMayLikeAdapter);
                        }
                        
                        moreAdapter = new RecommendationAdapter(ProductDetailActivity.this, products, wishlistProductIds);
                        binding.rvMoreProducts.setAdapter(moreAdapter);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<ProductModel>> call, @NonNull Throwable t) {}
        });
    }


    private void updateCartBadge() {
        // Method kept for potential future use, but UI elements removed from header
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

        binding.btnAddToCart.setOnClickListener(v -> {
            if (isAlreadyInCart) {
                removeFromCart();
            } else {
                if (binding.layoutSizeSelection.getVisibility() == View.VISIBLE && selectedSize.isEmpty()) {
                    Toast.makeText(this, "Please select a size", Toast.LENGTH_SHORT).show();
                    return;
                }
                addToCart();
            }
        });

        binding.btnWishlist.setOnClickListener(v -> toggleWishlist());
        binding.ivSearch.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        });

        binding.ivCart.setOnClickListener(v -> {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
        });

        binding.btnWriteReview.setOnClickListener(v -> showAddReviewDialog());

        binding.btnBuyNow.setOnClickListener(v -> {
            if (binding.layoutSizeSelection.getVisibility() == View.VISIBLE && selectedSize.isEmpty()) {
                Toast.makeText(this, "Please select a size", Toast.LENGTH_SHORT).show();
                return;
            }
            com.amstudio.lightbasket.utils.UserUtils.checkUserAddress(this, hasAddress -> {
                if (hasAddress) {
                    addToCartAndGoToCart();
                } else {
                    Toast.makeText(this, "Please add your delivery address first", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this, AddAddressActivity.class);
                    startActivity(intent);
                }
            });
        });
    }

    private void addToCartAndGoToCart() {
        if (product == null || product.getProductId() == null) {
            Toast.makeText(this, "Something went wrong. Please contact the developer.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isAlreadyInCart) {
            Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return;
        }

        binding.btnBuyNow.setEnabled(false);
        binding.btnBuyNow.setText("Processing...");

        com.amstudio.lightbasket.utils.CartHelper.addToCart(this, product, quantity, selectedSize, selectedPrice, selectedMrp, new com.amstudio.lightbasket.utils.CartHelper.CartCallback() {
            @Override
            public void onSuccess() {
                binding.btnBuyNow.setEnabled(true);
                binding.btnBuyNow.setText("Buy Now");
                isAlreadyInCart = true;
                updateCartUI();
                Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }

            @Override
            public void onFailure(String error) {
                binding.btnBuyNow.setEnabled(true);
                binding.btnBuyNow.setText("Buy Now");
                Toast.makeText(ProductDetailActivity.this, "Something went wrong. Please contact the developer.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToCart() {
        if (product == null || product.getProductId() == null) {
            Toast.makeText(this, "Something went wrong. Please contact the developer.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isAlreadyInCart) {
            removeFromCart();
            return;
        }

        binding.btnAddToCart.setEnabled(false);
        binding.btnAddToCart.setText("Adding...");

        com.amstudio.lightbasket.utils.CartHelper.addToCart(this, product, quantity, selectedSize, selectedPrice, selectedMrp, new com.amstudio.lightbasket.utils.CartHelper.CartCallback() {
            @Override
            public void onSuccess() {
                binding.btnAddToCart.setEnabled(true);
                Toast.makeText(ProductDetailActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                isAlreadyInCart = true;
                updateCartUI();
                updateCartBadge();
            }

            @Override
            public void onFailure(String error) {
                binding.btnAddToCart.setEnabled(true);
                updateCartUI();
                Toast.makeText(ProductDetailActivity.this, "Something went wrong. Please contact the developer.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkCartStatus() {
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
                    isAlreadyInCart = false; 
                    for (Map<String, Object> item : response.body()) {
                        Object pid = item.get("product_id") != null ? item.get("product_id") : item.get("PRODUCT_ID");
                        Object pSize = item.get("product_size");
                        
                        if (String.valueOf(pid).equals(product.getProductId())) {
                            // If user selected a size, check if it matches what's in cart
                            if (!selectedSize.isEmpty()) {
                                if (selectedSize.equals(String.valueOf(pSize))) {
                                    isAlreadyInCart = true;
                                    break;
                                }
                            } else {
                                isAlreadyInCart = true;
                                break;
                            }
                        }
                    }
                    updateCartUI();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Map<String, Object>>> call, @NonNull Throwable t) {}
        });
    }

    private void updateCartUI() {
        if (isAlreadyInCart) {
            binding.btnAddToCart.setText("Remove from Cart");
            binding.btnAddToCart.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.color_logout)));
        } else {
            binding.btnAddToCart.setText("🛒 Add to Cart");
            binding.btnAddToCart.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.primary_blue)));
        }
    }

    private void removeFromCart() {
        String userId = sessionManager.getUserId();
        if (userId.isEmpty()) return;

        String authHeader = "Bearer " + sessionManager.getToken();
        Map<String, String> filters = new HashMap<>();
        filters.put("user_id", "eq." + userId);
        filters.put("product_id", "eq." + product.getProductId());

        binding.btnAddToCart.setEnabled(false);
        binding.btnAddToCart.setText("Removing...");

        SupabaseClient.getApiService().deleteDataByFilters(
                "cart",
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                filters
        ).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                binding.btnAddToCart.setEnabled(true);
                if (response.isSuccessful()) {
                    isAlreadyInCart = false;
                    updateCartUI();
                    updateCartBadge();
                    Toast.makeText(ProductDetailActivity.this, "Removed from Cart", Toast.LENGTH_SHORT).show();
                } else {
                    updateCartUI();
                    Toast.makeText(ProductDetailActivity.this, "Something went wrong. Please contact the developer.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                binding.btnAddToCart.setEnabled(true);
                updateCartUI();
                Toast.makeText(ProductDetailActivity.this, "Something went wrong. Please contact the developer.", Toast.LENGTH_SHORT).show();
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
        ).enqueue(new Callback<List<com.amstudio.lightbasket.models.WishlistModel>>() {
            @Override
            public void onResponse(Call<List<com.amstudio.lightbasket.models.WishlistModel>> call, Response<List<com.amstudio.lightbasket.models.WishlistModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (com.amstudio.lightbasket.models.WishlistModel item : response.body()) {
                        if (String.valueOf(item.getProductId()).equals(product.getProductId())) {
                            isWishlisted = true;
                            updateWishlistUI();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<com.amstudio.lightbasket.models.WishlistModel>> call, Throwable t) {
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
        
        String rawPrice = product.getSellingPrice() != null ? product.getSellingPrice() : (product.getPrice() != null ? product.getPrice() : "0");
        double priceValue = com.amstudio.lightbasket.utils.PriceUtils.parsePrice(rawPrice);
        data.put("product_price", String.valueOf(priceValue));
        
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
            binding.btnWishlist.setIconResource(R.drawable.ic_heart_filled);
            binding.btnWishlist.setIconTintResource(R.color.color_logout);
            binding.btnWishlist.setText("Wishlisted");
            binding.btnWishlist.setTextColor(getResources().getColor(R.color.color_logout));
            binding.btnWishlist.setStrokeColor(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.color_logout)));
        } else {
            binding.btnWishlist.setIconResource(R.drawable.ic_heart_outline);
            binding.btnWishlist.setIconTintResource(R.color.primary_blue);
            binding.btnWishlist.setText("Wishlist");
            binding.btnWishlist.setTextColor(getResources().getColor(R.color.primary_blue));
            binding.btnWishlist.setStrokeColor(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.primary_blue)));
        }
    }
}

