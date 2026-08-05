package com.example.lightshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lightshop.api.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupWelcomeMessage();
        setupCategories();
        setupTopDeals();
        setupBottomNavigation();
        setupClickListeners();
    }

    private void setupWelcomeMessage() {
        TextView tvWelcome = findViewById(R.id.tv_welcome);
        SessionManager sessionManager = new SessionManager(this);
        String name = sessionManager.getUserName();
        tvWelcome.setText("Hi, " + name + " 👋");
    }

    private void setupClickListeners() {
        findViewById(R.id.tv_cat_view_all).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, CategoryActivity.class));
        });
    }

    private void setupCategories() {
        RecyclerView rvCategories = findViewById(R.id.rv_categories);
        List<HomeCategory> categories = new ArrayList<>();
        categories.add(new HomeCategory("Men", R.drawable.ic_men, R.color.cat_men_bg));
        categories.add(new HomeCategory("Women", R.drawable.ic_women, R.color.cat_women_bg));
        categories.add(new HomeCategory("Electronics", R.drawable.ic_electronics, R.color.cat_electronics_bg));
        categories.add(new HomeCategory("Home", R.drawable.ic_home_cat, R.color.cat_home_bg));
        categories.add(new HomeCategory("Beauty", R.drawable.ic_beauty, R.color.cat_beauty_bg));

        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(new HomeCategoryAdapter(categories));
    }

    private void setupTopDeals() {
        RecyclerView rvDeals = findViewById(R.id.rv_deals);
        List<Product> products = new ArrayList<>();
        products.add(new Product("Analog Watch", "₹599", "₹999", "-40%", R.drawable.ic_watch));
        products.add(new Product("Sports Shoes", "₹1,299", "₹1,999", "-35%", R.drawable.ic_shoes));
        products.add(new Product("Backpack", "₹749", "₹999", "-25%", R.drawable.ic_backpack));

        rvDeals.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvDeals.setAdapter(new ProductAdapter(products));
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_category) {
                startActivity(new Intent(HomeActivity.this, CategoryActivity.class));
                return false;
            } else if (itemId == R.id.nav_orders) {
                startActivity(new Intent(HomeActivity.this, MyOrdersActivity.class));
                return false;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                return false;
            }
            return false;
        });
    }

    // --- Data Models ---
    public static class HomeCategory {
        String name;
        int iconRes;
        int bgRes;

        HomeCategory(String name, int iconRes, int bgRes) {
            this.name = name;
            this.iconRes = iconRes;
            this.bgRes = bgRes;
        }
    }

    public static class Product {
        String name;
        String price;
        String oldPrice;
        String discount;
        int imageRes;

        Product(String name, String price, String oldPrice, String discount, int imageRes) {
            this.name = name;
            this.price = price;
            this.oldPrice = oldPrice;
            this.discount = discount;
            this.imageRes = imageRes;
        }
    }

    // --- Adapters ---
    public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.ViewHolder> {
        private List<HomeCategory> items;

        HomeCategoryAdapter(List<HomeCategory> items) {
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
            HomeCategory item = items.get(position);
            holder.name.setText(item.name);
            holder.icon.setImageResource(item.iconRes);
            if (holder.bg != null && holder.bg.getBackground() != null) {
                holder.bg.getBackground().setTint(holder.itemView.getContext().getColor(item.bgRes));
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
        private List<Product> items;

        ProductAdapter(List<Product> items) {
            this.items = items;
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
            Product item = items.get(position);
            holder.name.setText(item.name);
            holder.price.setText(item.price);
            holder.oldPrice.setText(item.oldPrice);
            holder.discount.setText(item.discount);
            holder.image.setImageResource(item.imageRes);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }
}
