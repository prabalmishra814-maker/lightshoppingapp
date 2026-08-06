package com.example.lightshop;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.lightshop.databinding.ActivityCategoryBinding;
import com.example.lightshop.utils.StatusBarUtils;
import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private ActivityCategoryBinding binding;
    private SidebarAdapter sidebarAdapter;
    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.applyWhiteStatusBar(this);
        binding = ActivityCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupSidebar();
        setupCategories();
        setupBottomNav();
    }

    private void setupSidebar() {
        List<Category> sidebarItems = new ArrayList<>();
        sidebarItems.add(new Category("All Categories", R.drawable.ic_all_categories));
        sidebarItems.add(new Category("Men", R.drawable.ic_men));
        sidebarItems.add(new Category("Women", R.drawable.ic_women));
        sidebarItems.add(new Category("Kids", R.drawable.ic_kids));
        sidebarItems.add(new Category("Electronics", R.drawable.ic_headphones));
        sidebarItems.add(new Category("Home", R.drawable.ic_home));
        sidebarItems.add(new Category("Beauty", R.drawable.ic_beauty));
        sidebarItems.add(new Category("Sports", R.drawable.ic_sports));
        sidebarItems.add(new Category("Automotive", R.drawable.ic_car));
        sidebarItems.add(new Category("Books", R.drawable.ic_book));
        sidebarItems.add(new Category("Grocery", R.drawable.ic_grocery));

        sidebarAdapter = new SidebarAdapter(sidebarItems, position -> {
            // Update main categories based on sidebar selection
            // For now, we'll just show different counts or filter if needed
        });

        binding.rvSidebar.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSidebar.setAdapter(sidebarAdapter);
    }

    private void setupCategories() {
        List<Category> categoryItems = new ArrayList<>();
        categoryItems.add(new Category("Men", "25,342 items", R.drawable.ic_men));
        categoryItems.add(new Category("Women", "32,142 items", R.drawable.ic_women));
        categoryItems.add(new Category("Kids", "12,532 items", R.drawable.ic_kids));
        categoryItems.add(new Category("Electronics", "18,231 items", R.drawable.ic_headphones));
        categoryItems.add(new Category("Home & Kitchen", "15,312 items", R.drawable.ic_home));
        categoryItems.add(new Category("Beauty", "9,213 items", R.drawable.ic_beauty));
        categoryItems.add(new Category("Sports", "7,432 items", R.drawable.ic_sports));
        categoryItems.add(new Category("Automotive", "5,421 items", R.drawable.ic_car));
        categoryItems.add(new Category("Books", "8,932 items", R.drawable.ic_book));
        categoryItems.add(new Category("Grocery", "6,102 items", R.drawable.ic_grocery));

        categoryAdapter = new CategoryAdapter(categoryItems);
        binding.rvCategories.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCategories.setAdapter(categoryAdapter);
    }

    private void setupBottomNav() {
        binding.bottomNav.setSelectedItemId(R.id.nav_category);
        binding.bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(CategoryActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_category) {
                return true;
            } else if (itemId == R.id.nav_orders) {
                startActivity(new Intent(CategoryActivity.this, MyOrdersActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(CategoryActivity.this, ProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }
}
