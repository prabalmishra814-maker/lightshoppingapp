package com.amstudio.lightbasket;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.amstudio.lightbasket.models.ProductModel;
import com.amstudio.lightbasket.models.CategoryModel;
import com.amstudio.lightbasket.api.SupabaseClient;
import com.amstudio.lightbasket.api.SessionManager;
import com.amstudio.lightbasket.utils.CartHelper;
import com.amstudio.lightbasket.utils.CartManager;
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

import com.facebook.shimmer.ShimmerFrameLayout;

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
        fetchUserAddress(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) {
            fetchUserAddress(getView());
            fetchCartProductIds();
        }
    }

    private void fetchUserAddress(View view) {
        String userId = sessionManager.getUserId();
        if (userId.isEmpty()) return;

        String authHeader = "Bearer " + sessionManager.getToken();
        java.util.Map<String, String> filters = new java.util.HashMap<>();
        filters.put("uid", "eq." + userId);
        filters.put("select", "address");

        SupabaseClient.getApiService().fetchDataWithFilters(
                "Users",
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                filters
        ).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(@NonNull Call<List<Map<String, Object>>> call, @NonNull Response<List<Map<String, Object>>> response) {
                TextView tvAddress = view.findViewById(R.id.tv_delivery_address);
                TextView tvChange = view.findViewById(R.id.tv_change_address);
                if (tvAddress == null) return;

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Object addressObj = response.body().get(0).get("address");
                    if (addressObj instanceof java.util.Map && !((java.util.Map) addressObj).isEmpty()) {
                        java.util.Map<String, Object> address = (java.util.Map<String, Object>) addressObj;
                        String city = (String) address.get("city");
                        String pincode = (String) address.get("pincode");
                        
                        if (city != null && pincode != null) {
                            tvAddress.setText("Deliver to " + city + " " + pincode);
                        } else if (pincode != null) {
                            tvAddress.setText("Deliver to " + pincode);
                        } else {
                            tvAddress.setText("Add your address");
                        }
                        if (tvChange != null) tvChange.setText(getString(R.string.change));
                    } else {
                        tvAddress.setText("Add your address");
                        if (tvChange != null) tvChange.setText("Add");
                    }
                } else {
                    tvAddress.setText("Add your address");
                    if (tvChange != null) tvChange.setText("Add");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Map<String, Object>>> call, @NonNull Throwable t) {
                TextView tvAddress = view.findViewById(R.id.tv_delivery_address);
                TextView tvChange = view.findViewById(R.id.tv_change_address);
                if (tvAddress != null) {
                    tvAddress.setText("Add your address");
                }
                if (tvChange != null) tvChange.setText("Add");
            }
        });
    }



    private void populateProfessionalHome(View view) {
        // 1. Categories
        RecyclerView rvCat = view.findViewById(R.id.rv_home_categories);
        if (rvCat != null) {
            rvCat.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            fetchCategoriesFromSupabase(view);
        }

        // 2. Best Selling (Now Explore Products Grid)
        RecyclerView rvBest = view.findViewById(R.id.rv_best_selling);
        if (rvBest != null) {
            rvBest.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(getContext(), 2));
            rvBest.setNestedScrollingEnabled(false);
        }
        
        fetchProductsFromSupabase(view); // Reusing existing fetch for both sections
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
                    notifyAdapters();
                }
            }

            @Override
            public void onFailure(Call<List<com.amstudio.lightbasket.models.WishlistModel>> call, Throwable t) {
                if (getContext() != null) {
                    android.widget.Toast.makeText(getContext(), "Something went wrong. Please contact the developer.", android.widget.Toast.LENGTH_SHORT).show();
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
        if (imageSlider == null) return;

        String authHeader = "Bearer " + SupabaseClient.SUPABASE_ANON_KEY;
        SupabaseClient.getApiService().fetchData(
                "banners",
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "*"
        ).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(@NonNull Call<List<Map<String, Object>>> call, @NonNull Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    ArrayList<SlideModel> imageList = new ArrayList<>();
                    for (Map<String, Object> map : response.body()) {
                        Object url = map.get("image_url");
                        if (url != null) {
                            imageList.add(new SlideModel(String.valueOf(url), ScaleTypes.FIT));
                        }
                    }
                    if (!imageList.isEmpty()) {
                        imageSlider.setImageList(imageList, ScaleTypes.FIT);
                    }
                } else {
                    // Fallback if table is empty
                    showDefaultBanners(imageSlider);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Map<String, Object>>> call, @NonNull Throwable t) {
                showDefaultBanners(imageSlider);
            }
        });
    }

    private void showDefaultBanners(ImageSlider imageSlider) {
        ArrayList<SlideModel> imageList = new ArrayList<>();
        imageList.add(new SlideModel("https://img.freepik.com/free-vector/shopping-day-banner-with-realistic-bags-shopping-cart-gifts-boxes_1361-2983.jpg", ScaleTypes.FIT));
        imageList.add(new SlideModel("https://img.freepik.com/free-vector/flat-shopping-background-with-sales_23-2149363065.jpg", ScaleTypes.FIT));
        imageList.add(new SlideModel("https://img.freepik.com/free-vector/gradient-sale-background_23-2148817454.jpg", ScaleTypes.FIT));
        imageSlider.setImageList(imageList, ScaleTypes.FIT);
    }

    private void fetchProductsFromSupabase(View view) {
        String authHeader = "Bearer " + SupabaseClient.SUPABASE_ANON_KEY;
        ShimmerFrameLayout shimmer = view.findViewById(R.id.shimmer_best_selling);
        
        SupabaseClient.getApiService().fetchProducts(
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "*"
        ).enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(Call<List<ProductModel>> call, Response<List<ProductModel>> response) {
                if (shimmer != null) {
                    shimmer.stopShimmer();
                    shimmer.setVisibility(View.GONE);
                }
                
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductModel> products = response.body();
                    
                    // Best Selling
                    RecyclerView rvBest = view.findViewById(R.id.rv_best_selling);
                    if (rvBest != null && getContext() != null) {
                        rvBest.setVisibility(View.VISIBLE);
                        allProductsAdapter = new ProductAdapter(getContext(), products, false, 
                            cartProductIds, wishlistProductIds, sessionManager, () -> updateHomeCartBadge(cartProductIds.size()));
                        rvBest.setAdapter(allProductsAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ProductModel>> call, Throwable t) {
                if (shimmer != null) {
                    shimmer.stopShimmer();
                    shimmer.setVisibility(View.GONE);
                }
            }
        });
    }

    private void fetchCategoriesFromSupabase(View view) {
        String authHeader = "Bearer " + SupabaseClient.SUPABASE_ANON_KEY;
        ShimmerFrameLayout shimmer = view.findViewById(R.id.shimmer_categories);
        
        SupabaseClient.getApiService().fetchCategories(
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "*"
        ).enqueue(new Callback<List<CategoryModel>>() {
            @Override
            public void onResponse(Call<List<CategoryModel>> call, Response<List<CategoryModel>> response) {
                if (shimmer != null) {
                    shimmer.stopShimmer();
                    shimmer.setVisibility(View.GONE);
                }
                
                if (response.isSuccessful() && response.body() != null) {
                    RecyclerView rvCat = view.findViewById(R.id.rv_home_categories);
                    if (rvCat != null && getContext() != null) {
                        rvCat.setVisibility(View.VISIBLE);
                        rvCat.setAdapter(new CategoryAdapter(response.body()));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<CategoryModel>> call, Throwable t) {
                if (shimmer != null) {
                    shimmer.stopShimmer();
                    shimmer.setVisibility(View.GONE);
                }
                Toast.makeText(getContext(), "Something went wrong. Please contact the developer.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void notifyAdapters() {
        if (allProductsAdapter != null) allProductsAdapter.notifyDataSetChanged();
    }

    private void setupClickListeners(View view) {
        View searchCard = view.findViewById(R.id.home_search_card);
        searchCard.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(getContext(), SearchActivity.class);
            if (getActivity() != null) {
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(), searchCard, "search_bar_transition");
                startActivity(intent, options.toBundle());
            } else {
                startActivity(intent);
            }
        });

        // Disable direct typing on home screen search bar to force move to SearchActivity
        EditText etHomeSearch = view.findViewById(R.id.et_home_search);
        if (etHomeSearch != null) {
            etHomeSearch.setFocusable(false);
            etHomeSearch.setClickable(true);
            etHomeSearch.setOnClickListener(v -> view.findViewById(R.id.home_search_card).performClick());
        }

        view.findViewById(R.id.home_cart_container).setOnClickListener(v -> {
            startActivity(new android.content.Intent(getContext(), CartActivity.class));
        });

        view.findViewById(R.id.tv_view_all_best).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(getContext(), ProductListActivity.class);
            intent.putExtra("title", "Explore Product");
            startActivity(intent);
        });


        view.findViewById(R.id.tv_change_address).setOnClickListener(v -> {
            startActivity(new android.content.Intent(getContext(), AddAddressActivity.class));
        });

        view.findViewById(R.id.iv_home_notification).setOnClickListener(v -> {
            startActivity(new android.content.Intent(getContext(), NotificationActivity.class));
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
            
            loadImage(item.getCategoryImage(), holder.icon);

            holder.itemView.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(getContext(), ProductListActivity.class);
                intent.putExtra("category", item.getCategoryName());
                intent.putExtra("title", item.getCategoryName());
                startActivity(intent);
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

}

