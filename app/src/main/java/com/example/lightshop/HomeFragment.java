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
import androidx.recyclerview.widget.GridLayoutManager;
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
        fetchCategoriesFromSupabase(view);
        fetchCartProductIds();
        fetchWishlistProductIds();
        fetchProductsFromSupabase(view);
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
                    for (Map<String, Object> item : response.body()) {
                        Object pid = item.get("PRODUCT_ID") != null ? item.get("PRODUCT_ID") : item.get("product_id");
                        if (pid != null) {
                            cartProductIds.add(String.valueOf(pid));
                        }
                    }
                    if (allProductsAdapter != null) {
                        allProductsAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                if (getContext() != null) {
                    android.widget.Toast.makeText(getContext(), "Cart Error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupImageSlider(View view) {
        ImageSlider imageSlider = view.findViewById(R.id.image_slider);
        if (imageSlider != null) {
            ArrayList<SlideModel> imageList = new ArrayList<>();
            // Only images, no titles
            imageList.add(new SlideModel("https://bit.ly/2YoJ77H", ScaleTypes.FIT));
            imageList.add(new SlideModel("https://bit.ly/2BteuF2", ScaleTypes.FIT));
            imageList.add(new SlideModel("https://bit.ly/3fLJf72", ScaleTypes.FIT));
            
            imageSlider.setImageList(imageList, ScaleTypes.FIT);
        }
    }

    private void fetchCategoriesFromSupabase(View view) {
        String authHeader = "Bearer " + SupabaseClient.SUPABASE_ANON_KEY;
        SupabaseClient.getApiService().fetchCategories(
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "*"
        ).enqueue(new Callback<List<CategoryModel>>() {
            @Override
            public void onResponse(Call<List<CategoryModel>> call, Response<List<CategoryModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CategoryModel> categories = response.body();
                    RecyclerView rvCategories = view.findViewById(R.id.rv_categories);
                    if (rvCategories != null) {
                        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                        rvCategories.setAdapter(new CategoryAdapter(categories));
                    }
                } else {
                    if (response.code() == 404) {
                        fetchCategoriesPluralFromSupabase(view);
                        return;
                    }
                    // Fallback to hardcoded categories if table is empty or missing
                    if (getContext() != null) {
                        android.widget.Toast.makeText(getContext(), "Categories Table Error: " + response.code(), android.widget.Toast.LENGTH_SHORT).show();
                    }
                    setupHardcodedCategories(view);
                }
            }

            @Override
            public void onFailure(Call<List<CategoryModel>> call, Throwable t) {
                android.util.Log.e("SupabaseError", "Categories Fetch Failed", t);
                // Try plural on failure too if it might be a DNS/URL issue related to path
                fetchCategoriesPluralFromSupabase(view);
            }
        });
    }

    private void fetchCategoriesPluralFromSupabase(View view) {
        String authHeader = "Bearer " + SupabaseClient.SUPABASE_ANON_KEY;
        SupabaseClient.getApiService().fetchCategoriesPlural(
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "*"
        ).enqueue(new Callback<List<CategoryModel>>() {
            @Override
            public void onResponse(Call<List<CategoryModel>> call, Response<List<CategoryModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CategoryModel> categories = response.body();
                    RecyclerView rvCategories = view.findViewById(R.id.rv_categories);
                    if (rvCategories != null) {
                        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                        rvCategories.setAdapter(new CategoryAdapter(categories));
                    }
                } else {
                    if (getContext() != null) {
                        android.widget.Toast.makeText(getContext(), "Categories Plural Error: " + response.code(), android.widget.Toast.LENGTH_SHORT).show();
                    }
                    setupHardcodedCategories(view);
                }
            }

            @Override
            public void onFailure(Call<List<CategoryModel>> call, Throwable t) {
                android.util.Log.e("SupabaseError", "Categories Plural Fetch Failed", t);
                setupHardcodedCategories(view);
            }
        });
    }

    private void setupHardcodedCategories(View view) {
        RecyclerView rvCategories = view.findViewById(R.id.rv_categories);
        List<HomeActivity.HomeCategory> categories = new ArrayList<>();
        categories.add(new HomeActivity.HomeCategory("Men", R.drawable.ic_men, R.color.cat_men_bg));
        categories.add(new HomeActivity.HomeCategory("Women", R.drawable.ic_women, R.color.cat_women_bg));
        categories.add(new HomeActivity.HomeCategory("Electronics", R.drawable.ic_electronics, R.color.cat_electronics_bg));
        categories.add(new HomeActivity.HomeCategory("Home", R.drawable.ic_home_cat, R.color.cat_home_bg));
        categories.add(new HomeActivity.HomeCategory("Beauty", R.drawable.ic_beauty, R.color.cat_beauty_bg));

        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(new HomeCategoryAdapter(categories));
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
                    
                    // Setup All Products (Grid)
                    RecyclerView rvAll = view.findViewById(R.id.rv_all_products);
                    if (rvAll != null) {
                        rvAll.setLayoutManager(new GridLayoutManager(getContext(), 2));
                        allProductsAdapter = new ProductAdapter(products, false);
                        rvAll.setAdapter(allProductsAdapter);
                        rvAll.setNestedScrollingEnabled(false); // Since it's inside NestedScrollView
                    }
                } else {
                    if (response.code() == 404) {
                        fetchProductsPluralFromSupabase(view);
                        return;
                    }
                    if (getContext() != null) {
                        android.widget.Toast.makeText(getContext(), "Products Table Error: " + response.code(), android.widget.Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ProductModel>> call, Throwable t) {
                android.util.Log.e("SupabaseError", "Products Fetch Failed", t);
                fetchProductsPluralFromSupabase(view);
            }
        });
    }

    private void fetchProductsPluralFromSupabase(View view) {
        String authHeader = "Bearer " + SupabaseClient.SUPABASE_ANON_KEY;
        SupabaseClient.getApiService().fetchProductsPlural(
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "*"
        ).enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(Call<List<ProductModel>> call, Response<List<ProductModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductModel> products = response.body();
                    RecyclerView rvAll = view.findViewById(R.id.rv_all_products);
                    if (rvAll != null) {
                        rvAll.setLayoutManager(new GridLayoutManager(getContext(), 2));
                        allProductsAdapter = new ProductAdapter(products, false);
                        rvAll.setAdapter(allProductsAdapter);
                        rvAll.setNestedScrollingEnabled(false);
                    }
                } else {
                    if (getContext() != null) {
                        android.widget.Toast.makeText(getContext(), "Products Plural Error: " + response.code(), android.widget.Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ProductModel>> call, Throwable t) {
                android.util.Log.e("SupabaseError", "Products Plural Fetch Failed", t);
                if (getContext() != null) {
                    android.widget.Toast.makeText(getContext(), "Showing offline products", android.widget.Toast.LENGTH_SHORT).show();
                }
                setupHardcodedProducts(view);
            }
        });
    }

    private void setupHardcodedProducts(View view) {
        List<ProductModel> dummyProducts = new ArrayList<>();
        
        ProductModel p1 = new ProductModel();
        p1.setProductId("dummy1");
        p1.setProductName("boAt Airdopes 141 Pro");
        p1.setSellingPrice("1299");
        p1.setMrp("1999");
        p1.setProductImage("https://m.media-amazon.com/images/I/315vj6oj-FL._MCnd_AC_CEL_SR450,600_.jpg");
        p1.setRating("4.3");
        p1.setReviewsCount("12,456");
        p1.setSoldCount("1.2L+");
        p1.setDescription("Premium Bluetooth earbuds with 42 hours playtime and ASAP charge.");
        dummyProducts.add(p1);

        ProductModel p2 = new ProductModel();
        p2.setProductId("dummy2");
        p2.setProductName("Noise Pulse 2 Max");
        p2.setSellingPrice("1799");
        p2.setMrp("5999");
        p2.setProductImage("https://m.media-amazon.com/images/I/71Scpa62s+L._SX679_.jpg");
        p2.setRating("4.1");
        p2.setReviewsCount("8,234");
        p2.setSoldCount("50K+");
        p2.setDescription("1.85'' Display, Bluetooth Calling, 10 Days Battery Smartwatch.");
        dummyProducts.add(p2);

        RecyclerView rvAll = view.findViewById(R.id.rv_all_products);
        if (rvAll != null) {
            rvAll.setLayoutManager(new GridLayoutManager(getContext(), 2));
            allProductsAdapter = new ProductAdapter(dummyProducts, false);
            rvAll.setAdapter(allProductsAdapter);
            rvAll.setNestedScrollingEnabled(false);
        }
    }

    private void setupClickListeners(View view) {
        view.findViewById(R.id.tv_cat_view_all).setOnClickListener(v -> {
            if (getActivity() instanceof HomeActivity) {
                ((HomeActivity) getActivity()).switchToCategory();
            }
        });

        view.findViewById(R.id.cart_button_card).setOnClickListener(v -> {
            startActivity(new android.content.Intent(getContext(), CartActivity.class));
        });

        view.findViewById(R.id.wishlist_card).setOnClickListener(v -> {
            startActivity(new android.content.Intent(getContext(), WishlistActivity.class));
        });
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
            
            Glide.with(getContext())
                    .load(item.getCategoryImage())
                    .placeholder(R.drawable.ic_category)
                    .error(R.drawable.ic_category)
                    .into(holder.icon);

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
            if (holder.bg != null && holder.bg.getBackground() != null) {
                holder.bg.getBackground().setTint(getContext().getColor(item.bgRes));
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

            Glide.with(getContext())
                    .load(item.getProductImage())
                    .placeholder(R.drawable.ic_category)
                    .error(R.drawable.ic_category)
                    .into(holder.image);

            if (holder.wishlist != null) {
                if (wishlistProductIds.contains(item.getProductId())) {
                    holder.wishlist.setImageResource(R.drawable.ic_heart_filled);
                    holder.wishlist.setColorFilter(null); // Use original color (#FF0000)
                } else {
                    holder.wishlist.setImageResource(R.drawable.ic_heart_outline);
                    holder.wishlist.setColorFilter(getResources().getColor(R.color.text_subtitle));
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
                    } else if (response.code() == 409) {
                        // Already handled by toggle logic
                    } else {
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
