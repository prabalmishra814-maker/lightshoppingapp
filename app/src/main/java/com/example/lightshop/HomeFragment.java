package com.example.lightshop;

import android.content.Intent;
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
import com.example.lightshop.api.SupabaseClient;
import com.bumptech.glide.Glide;
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

        setupCategories(view);
        setupClickListeners(view);
        fetchProductsFromSupabase(view);
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

    private void setupCategories(View view) {
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

    // --- Adapters (Copied from HomeActivity) ---
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

            ViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.tv_product_name);
                price = view.findViewById(R.id.tv_price);
                oldPrice = view.findViewById(R.id.tv_old_price);
                discount = view.findViewById(R.id.tv_discount);
                image = view.findViewById(R.id.iv_product);
                
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
            
            String sellingPrice = item.getSellingPrice();
            String mrp = item.getMrp();
            
            holder.price.setText("₹" + (sellingPrice != null ? sellingPrice : "0"));
            holder.oldPrice.setText("₹" + (mrp != null ? mrp : "0"));
            
            // Dynamic discount calculation
            try {
                if (sellingPrice != null && mrp != null) {
                    double sPrice = Double.parseDouble(sellingPrice.replaceAll("[^0-9.]", ""));
                    double mPrice = Double.parseDouble(mrp.replaceAll("[^0-9.]", ""));
                    if (mPrice > sPrice) {
                        int discountPercent = (int) (((mPrice - sPrice) / mPrice) * 100);
                        holder.discount.setText("-" + discountPercent + "%");
                        holder.discount.setVisibility(View.VISIBLE);
                    } else {
                        holder.discount.setVisibility(View.GONE);
                    }
                } else {
                    holder.discount.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                holder.discount.setVisibility(View.GONE);
            }

            Glide.with(getContext())
                    .load(item.getProductImage())
                    .placeholder(R.drawable.ic_category)
                    .error(R.drawable.ic_category)
                    .into(holder.image);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }
}
