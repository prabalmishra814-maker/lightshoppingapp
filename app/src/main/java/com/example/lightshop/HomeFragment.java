package com.example.lightshop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lightshop.models.ProductModel;
import com.example.lightshop.models.CategoryModel;
import com.example.lightshop.api.SupabaseClient;
import com.example.lightshop.api.SessionManager;
import com.example.lightshop.utils.CartHelper;
import com.example.lightshop.utils.CartManager;
import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import okhttp3.ResponseBody;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private SessionManager sessionManager;
    private Set<String> cartProductIds = new HashSet<>();
    private Set<String> wishlistProductIds = new HashSet<>();
    private ProductAdapter allProductsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        setupClickListeners(view);
        setupImageSlider(view);

        populateProfessionalHome(view);
        fetchCartProductIds();
        fetchWishlistProductIds();
    }



    private void populateProfessionalHome(View view) {
        // 1. Professional Categories
        RecyclerView rvCat = view.findViewById(R.id.rv_home_categories);
        List<HomeActivity.HomeCategory> catList = new ArrayList<>();
        catList.add(new HomeActivity.HomeCategory("Top Offers", R.drawable.ic_all_categories, R.color.white));
        catList.add(new HomeActivity.HomeCategory("Mobiles", R.drawable.ic_electronics, R.color.white));
        catList.add(new HomeActivity.HomeCategory("Fashion", R.drawable.ic_men, R.color.white));
        catList.add(new HomeActivity.HomeCategory("Electronics", R.drawable.ic_headphones, R.color.white));
        catList.add(new HomeActivity.HomeCategory("Home", R.drawable.ic_home_cat, R.color.white));
        catList.add(new HomeActivity.HomeCategory("Beauty", R.drawable.ic_beauty, R.color.white));
        
        rvCat.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCat.setAdapter(new HomeCategoryAdapter(catList));

        // 2. Sample Deals of the Day (to match image)
        RecyclerView rvDeals = view.findViewById(R.id.rv_deals);
        List<ProductModel> dealList = new ArrayList<>();
        
        ProductModel d1 = new ProductModel();
        d1.setProductId("deal1");
        d1.setProductName("Noise ColorFit Pulse 2 Max");
        d1.setSellingPrice("1799");
        d1.setPrice("1799");
        d1.setMrp("2499");
        d1.setMainPrice("2499");
        d1.setRating("4.4");
        d1.setReviewsCount("5.6K");
        d1.setProductImage("https://m.media-amazon.com/images/I/61X-u4fX1yL._SX679_.jpg");
        dealList.add(d1);

        ProductModel d2 = new ProductModel();
        d2.setProductId("deal2");
        d2.setProductName("boAt Airdopes 141 Pro");
        d2.setSellingPrice("1299");
        d2.setPrice("1299");
        d2.setMrp("1999");
        d2.setMainPrice("1999");
        d2.setRating("4.3");
        d2.setReviewsCount("2.3K");
        d2.setProductImage("https://m.media-amazon.com/images/I/315vj6oj-FL._MCnd_AC_CEL_SR450,600_.jpg");
        dealList.add(d2);

        ProductModel d3 = new ProductModel();
        d3.setProductId("deal3");
        d3.setProductName("Campus Running Shoes");
        d3.setSellingPrice("1499");
        d3.setPrice("1499");
        d3.setMrp("1999");
        d3.setMainPrice("1999");
        d3.setRating("4.2");
        d3.setReviewsCount("1.2K");
        d3.setProductImage("https://m.media-amazon.com/images/I/61+9EwG96VL._SY695_.jpg");
        dealList.add(d3);

        rvDeals.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvDeals.setAdapter(new DealAdapter(dealList));

        // 3. Best Selling
        RecyclerView rvBest = view.findViewById(R.id.rv_best_selling);
        rvBest.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        fetchProductsFromSupabase(view); // Reusing existing fetch for Best Selling
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
                    if (allProductsAdapter != null) {
                        allProductsAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<com.example.lightshop.models.WishlistModel>> call, Throwable t) {
                if (getContext() != null) {
                    android.widget.Toast.makeText(getContext(), "Wishlist Error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                }
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
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cartProductIds.clear();
                    for (Map<String, Object> item : response.body()) {
                        Object pid = item.get("PRODUCT_ID") != null ? item.get("PRODUCT_ID") : item.get("product_id");
                        if (pid != null) {
                            cartProductIds.add(String.valueOf(pid));
                        }
                    }
                    updateHomeCartBadge(cartProductIds.size());
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {}
        });
    }

    private void updateHomeCartBadge(int count) {
        if (getView() == null) return;
        TextView badge = getView().findViewById(R.id.tv_home_cart_badge);
        if (badge != null) {
            if (count > 0) {
                badge.setText(String.valueOf(count));
                badge.setVisibility(View.VISIBLE);
            } else {
                badge.setVisibility(View.GONE);
            }
        }
    }

    private void setupImageSlider(View view) {
        ImageSlider imageSlider = view.findViewById(R.id.home_image_slider);
        if (imageSlider != null) {
            ArrayList<SlideModel> imageList = new ArrayList<>();
            // High quality professional banners
            imageList.add(new SlideModel("https://img.freepik.com/free-vector/shopping-day-banner-with-realistic-bags-shopping-cart-gifts-boxes_1361-2983.jpg", ScaleTypes.FIT));
            imageList.add(new SlideModel("https://img.freepik.com/free-vector/flat-shopping-background-with-sales_23-2149363065.jpg", ScaleTypes.FIT));
            imageList.add(new SlideModel("https://img.freepik.com/free-vector/gradient-sale-background_23-2148817454.jpg", ScaleTypes.FIT));
            
            imageSlider.setImageList(imageList, ScaleTypes.FIT);
        }
    }

    private void fetchProductsFromSupabase(View view) {
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
                    RecyclerView rvBest = view.findViewById(R.id.rv_best_selling);
                    if (rvBest != null && getContext() != null) {
                        // Horizontal scroll for Best Selling
                        rvBest.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                        allProductsAdapter = new ProductAdapter(products, true);
                        rvBest.setAdapter(allProductsAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ProductModel>> call, Throwable t) {}
        });
    }

    private void setupClickListeners(View view) {
        view.findViewById(R.id.home_cart_container).setOnClickListener(v -> {
            startActivity(new android.content.Intent(getContext(), CartActivity.class));
        });

        view.findViewById(R.id.tv_view_all_best).setOnClickListener(v -> {
            if (getActivity() instanceof HomeActivity) {
                ((HomeActivity) getActivity()).switchToCategory();
            }
        });


        view.findViewById(R.id.iv_home_notification).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Notifications", Toast.LENGTH_SHORT).show();
        });
    }

    // --- Deal Adapter ---
    public class DealAdapter extends RecyclerView.Adapter<DealAdapter.ViewHolder> {
        private List<ProductModel> items;

        DealAdapter(List<ProductModel> items) {
            this.items = items;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, price, oldPrice, rating, reviews, discount;
            ImageView image;

            ViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.tv_deal_name);
                price = view.findViewById(R.id.tv_deal_price);
                oldPrice = view.findViewById(R.id.tv_deal_old_price);
                rating = view.findViewById(R.id.tv_deal_rating);
                reviews = view.findViewById(R.id.tv_deal_reviews);
                discount = view.findViewById(R.id.tv_deal_discount);
                image = view.findViewById(R.id.iv_deal_product);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_deal, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ProductModel item = items.get(position);
            holder.name.setText(item.getProductName());
            
            String priceStr = item.getSellingPrice() != null ? item.getSellingPrice() : (item.getPrice() != null ? item.getPrice() : "0");
            String mrpStr = item.getMrp() != null ? item.getMrp() : (item.getMainPrice() != null ? item.getMainPrice() : priceStr);
            
            holder.price.setText("₹" + priceStr);
            holder.oldPrice.setText("₹" + mrpStr);
            holder.oldPrice.setPaintFlags(holder.oldPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            holder.rating.setText(item.getRating() != null ? item.getRating() : "4.0");
            holder.reviews.setText("(" + (item.getReviewsCount() != null ? item.getReviewsCount() : "0") + ")");
            
            // Calculate discount percentage
            try {
                double s = Double.parseDouble(priceStr.replaceAll("[^0-9.]", ""));
                double m = Double.parseDouble(mrpStr.replaceAll("[^0-9.]", ""));
                if (m > 0 && m > s) {
                    int d = (int) (((m - s) / m) * 100);
                    holder.discount.setText("-" + d + "%");
                    holder.discount.setVisibility(View.VISIBLE);
                } else {
                    holder.discount.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                holder.discount.setVisibility(View.GONE);
            }

            Glide.with(getContext()).load(item.getProductImage()).into(holder.image);

            holder.itemView.setOnClickListener(v -> {
                if (getActivity() instanceof HomeActivity) {
                    ((HomeActivity) getActivity()).openProductDetail(item);
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    // --- Adapters ---
    public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
        private List<CategoryModel> items;

        CategoryAdapter(List<CategoryModel> items) {
            this.items = items;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            ImageView icon;
            View bg;

            ViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.cat_name);
                icon = view.findViewById(R.id.cat_icon);
                bg = view.findViewById(R.id.cat_bg);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CategoryModel item = items.get(position);
            holder.name.setText(item.getCategoryName());
            
            loadImage(item.getCategoryImage(), holder.icon);

            holder.itemView.setOnClickListener(v -> {
                if (getActivity() instanceof HomeActivity) {
                    ((HomeActivity) getActivity()).switchToCategory();
                }
            });
        }

        private void loadImage(String imageSource, ImageView imageView) {
            if (imageSource == null || imageSource.isEmpty()) {
                imageView.setImageResource(R.drawable.ic_category);
                return;
            }

            if (imageSource.startsWith("http")) {
                Glide.with(getContext())
                        .load(imageSource)
                        .placeholder(R.drawable.ic_category)
                        .error(R.drawable.ic_category)
                        .into(imageView);
            } else {
                int resId = getResources().getIdentifier(imageSource, "drawable", requireContext().getPackageName());
                if (resId != 0) {
                    Glide.with(getContext())
                            .load(resId)
                            .placeholder(R.drawable.ic_category)
                            .into(imageView);
                } else {
                    Glide.with(getContext())
                            .load(imageSource)
                            .placeholder(R.drawable.ic_category)
                            .error(R.drawable.ic_category)
                            .into(imageView);
                }
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.ViewHolder> {
        private List<HomeActivity.HomeCategory> items;

        HomeCategoryAdapter(List<HomeActivity.HomeCategory> items) {
            this.items = items;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            ImageView icon;
            View bg;

            ViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.cat_name);
                icon = view.findViewById(R.id.cat_icon);
                bg = view.findViewById(R.id.cat_bg);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            HomeActivity.HomeCategory item = items.get(position);
            holder.name.setText(item.name);
            holder.icon.setImageResource(item.iconRes);
            if (holder.bg != null && holder.bg.getBackground() != null && getContext() != null) {
                holder.bg.getBackground().setTint(androidx.core.content.ContextCompat.getColor(getContext(), item.bgRes));
            }

            holder.itemView.setOnClickListener(v -> {
                if (getActivity() instanceof HomeActivity) {
                    ((HomeActivity) getActivity()).switchToCategory();
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
        private final List<ProductModel> items;
        private final boolean isHorizontal;

        ProductAdapter(List<ProductModel> items, boolean isHorizontal) {
            this.items = items;
            this.isHorizontal = isHorizontal;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            TextView price;
            TextView oldPrice;
            TextView discount;
            ImageView image;
            ImageView wishlist;
            ImageView ivAddIcon;
            View btnAdd;

            ViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.tv_product_name);
                price = view.findViewById(R.id.tv_price);
                oldPrice = view.findViewById(R.id.tv_old_price);
                discount = view.findViewById(R.id.tv_discount);
                image = view.findViewById(R.id.iv_product);
                wishlist = view.findViewById(R.id.iv_wishlist);
                btnAdd = view.findViewById(R.id.btn_add_to_cart);
                ivAddIcon = view.findViewById(R.id.iv_add_icon);
                
                // Adjust layout for grid if needed
                if (!isHorizontal) {
                    ViewGroup.LayoutParams params = itemView.getLayoutParams();
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    itemView.setLayoutParams(params);
                }
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ProductModel item = items.get(position);
            holder.name.setText(item.getProductName());
            
            // product_price is the selling price (to buy)
            String sellingPriceStr = item.getPrice();
            // product_main_price is the previous price (original/MRP)
            String mrpStr = item.getMainPrice();
            
            // Fallback if null
            if (sellingPriceStr == null) sellingPriceStr = "0";
            if (mrpStr == null) mrpStr = sellingPriceStr;

            // Format prices as integers and calculate discount
            try {
                String sClean = sellingPriceStr != null ? sellingPriceStr.replaceAll("[^0-9.]", "") : "";
                String mClean = mrpStr != null ? mrpStr.replaceAll("[^0-9.]", "") : "";

                if (!sClean.isEmpty() && !mClean.isEmpty()) {
                    double sPrice = Double.parseDouble(sClean);
                    double mPrice = Double.parseDouble(mClean);
                    
                    // Show only Integer value (no decimals)
                    holder.price.setText("₹" + (int) sPrice);
                    holder.oldPrice.setText("₹" + (int) mPrice);
                    holder.oldPrice.setPaintFlags(holder.oldPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);

                    // Discount Formula: ((MRP - SellingPrice) / MRP) * 100
                    if (mPrice > sPrice && mPrice > 0) {
                        double discount = ((mPrice - sPrice) / mPrice) * 100;
                        int discountPercent = (int) Math.round(discount);
                        
                        if (discountPercent > 0) {
                            holder.discount.setText(discountPercent + "% OFF");
                            holder.discount.setVisibility(View.VISIBLE);
                        } else {
                            holder.discount.setVisibility(View.GONE);
                        }
                    } else {
                        holder.discount.setVisibility(View.GONE);
                    }
                } else {
                    holder.price.setText("₹" + (sellingPriceStr != null && !sellingPriceStr.isEmpty() ? sellingPriceStr : "0"));
                    holder.oldPrice.setText("₹" + (mrpStr != null && !mrpStr.isEmpty() ? mrpStr : "0"));
                    holder.discount.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                holder.price.setText("₹" + (sellingPriceStr != null ? sellingPriceStr : "0"));
                holder.oldPrice.setText("₹" + (mrpStr != null ? mrpStr : "0"));
                holder.discount.setVisibility(View.GONE);
            }

            loadImage(item.getProductImage(), holder.image);

            if (holder.wishlist != null) {
                if (wishlistProductIds.contains(item.getProductId())) {
                    holder.wishlist.setImageResource(R.drawable.ic_heart_filled);
                    holder.wishlist.setColorFilter(null); // Use original color (#FF0000)
                } else {
                    holder.wishlist.setImageResource(R.drawable.ic_heart_outline);
                    holder.wishlist.setColorFilter(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_subtitle));
                }

                holder.wishlist.setOnClickListener(v -> {
                    if (wishlistProductIds.contains(item.getProductId())) {
                        removeFromWishlist(item);
                    } else {
                        addToWishlist(item);
                    }
                });
            }

            if (holder.btnAdd != null) {
                if (cartProductIds.contains(item.getProductId())) {
                    holder.ivAddIcon.setImageResource(R.drawable.ic_check);
                } else {
                    holder.ivAddIcon.setImageResource(R.drawable.ic_add);
                }
                holder.btnAdd.setOnClickListener(v -> {
                    if (cartProductIds.contains(item.getProductId())) {
                        removeFromCart(item);
                    } else {
                        addToCart(item);
                    }
                });
            }

            holder.itemView.setOnClickListener(v -> {
                if (getActivity() instanceof HomeActivity) {
                    ((HomeActivity) getActivity()).openProductDetail(item);
                }
            });
        }

        private void loadImage(String imageSource, ImageView imageView) {
            if (imageSource == null || imageSource.isEmpty()) {
                imageView.setImageResource(R.drawable.ic_category);
                return;
            }

            if (imageSource.startsWith("http")) {
                Glide.with(getContext())
                        .load(imageSource)
                        .placeholder(R.drawable.ic_category)
                        .error(R.drawable.ic_category)
                        .into(imageView);
            } else {
                int resId = getResources().getIdentifier(imageSource, "drawable", requireContext().getPackageName());
                if (resId != 0) {
                    Glide.with(getContext())
                            .load(resId)
                            .placeholder(R.drawable.ic_category)
                            .into(imageView);
                } else {
                    Glide.with(getContext())
                            .load(imageSource)
                            .placeholder(R.drawable.ic_category)
                            .error(R.drawable.ic_category)
                            .into(imageView);
                }
            }
        }

        private void removeFromCart(ProductModel product) {
            String userId = sessionManager.getUserId();
            if (userId.isEmpty()) return;

            String authHeader = "Bearer " + sessionManager.getToken();
            Map<String, String> filters = new HashMap<>();
            filters.put("user_id", "eq." + userId);
            filters.put("product_id", "eq." + product.getProductId());

            SupabaseClient.getApiService().deleteDataByFilters(
                    "cart",
                    SupabaseClient.SUPABASE_ANON_KEY,
                    authHeader,
                    filters
            ).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        cartProductIds.remove(product.getProductId());
                        // Sync with CartManager
                        for (int i = 0; i < CartManager.getInstance().getCartItems().size(); i++) {
                            if (CartManager.getInstance().getCartItems().get(i).getProduct().getProductId().equals(product.getProductId())) {
                                CartManager.getInstance().removeItem(i);
                                break;
                            }
                        }
                        notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    android.widget.Toast.makeText(getContext(), "Failed to remove: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void addToCart(ProductModel product) {
            CartHelper.addToCart(getContext(), product, new CartHelper.CartCallback() {
                @Override
                public void onSuccess() {
                    cartProductIds.add(product.getProductId());
                    notifyDataSetChanged();
                    Toast.makeText(getContext(), "Added to Cart", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(getContext(), "Failed: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void addToWishlist(ProductModel product) {
            String userId = sessionManager.getUserId();
            if (userId.isEmpty()) {
                android.widget.Toast.makeText(getContext(), "Please login to add to wishlist", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> data = new HashMap<>();
            data.put("user_id", userId);
            data.put("product_id", product.getProductId());
            data.put("product_name", product.getProductName());
            data.put("product_price", product.getSellingPrice() != null ? product.getSellingPrice() : product.getPrice());
            data.put("product_image", product.getProductImage());

            String userToken = sessionManager.getToken();
            String authHeader = "Bearer " + (userToken != null && !userToken.isEmpty() ? userToken : SupabaseClient.SUPABASE_ANON_KEY);
            
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
                        wishlistProductIds.add(product.getProductId());
                        notifyDataSetChanged();
                    } else if (response.code() != 409) {
                        android.widget.Toast.makeText(getContext(), "Error: " + response.code(), android.widget.Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Map<String, Object>>> call, @NonNull Throwable t) {
                    android.widget.Toast.makeText(getContext(), "Error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void removeFromWishlist(ProductModel product) {
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
                        wishlistProductIds.remove(product.getProductId());
                        notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    android.widget.Toast.makeText(getContext(), "Failed to remove: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }
}
