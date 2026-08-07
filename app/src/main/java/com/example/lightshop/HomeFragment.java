package com.example.lightshop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lightshop.models.ProductModel;
import com.example.lightshop.models.CategoryModel;
import com.example.lightshop.api.SupabaseClient;
import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupClickListeners(view);
        setupImageSlider(view);
        fetchCategoriesFromSupabase(view);
        fetchProductsFromSupabase(view);
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
                    // Fallback to hardcoded categories if table is empty or missing
                    setupHardcodedCategories(view);
                }
            }

            @Override
            public void onFailure(Call<List<CategoryModel>> call, Throwable t) {
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
                        rvAll.setAdapter(new ProductAdapter(products, false));
                        rvAll.setNestedScrollingEnabled(false); // Since it's inside NestedScrollView
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ProductModel>> call, Throwable t) {
                // Error handling
            }
        });
    }

    private void setupClickListeners(View view) {
        view.findViewById(R.id.tv_cat_view_all).setOnClickListener(v -> {
            // In a single activity app, we would usually switch fragments here.
            // But the user didn't specify changing this behavior yet. 
            // For now, I'll keep it as a Fragment switch if the host can handle it, 
            // but the prompt asked to refactor the app to use Fragments.
            // Let's assume we want to navigate to the Category fragment.
            if (getActivity() instanceof HomeActivity) {
                ((HomeActivity) getActivity()).switchToCategory();
            }
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
            
            String sellingPriceStr = item.getSellingPrice();
            String mrpStr = item.getMrp();
            
            // Fallback if mrp or sellingPrice are null
            if (sellingPriceStr == null) sellingPriceStr = item.getPrice();
            if (mrpStr == null) mrpStr = item.getMainPrice();

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
                holder.wishlist.setOnClickListener(v -> {
                    // Toggle wishlist logic
                });
            }

            if (holder.btnAdd != null) {
                holder.btnAdd.setOnClickListener(v -> {
                    // Add to cart logic
                });
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }
}
