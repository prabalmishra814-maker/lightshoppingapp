package com.example.lightshop;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.lightshop.utils.StatusBarUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.applyWhiteStatusBar(this);
        setContentView(R.layout.activity_home);

        setupBottomNavigation();

        if (savedInstanceState == null) {
            String navigateTo = getIntent().getStringExtra("navigate_to");
            if ("orders".equals(navigateTo)) {
                switchToOrders();
            } else {
                loadFragment(new HomeFragment());
            }
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment selectedFragment = null;

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_category) {
                selectedFragment = new CategoryFragment();
            } else if (itemId == R.id.nav_orders) {
                selectedFragment = new MyOrdersFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public void openProductDetail(com.example.lightshop.models.ProductModel product) {
        android.content.Intent intent = new android.content.Intent(this, ProductDetailActivity.class);
        intent.putExtra("product", product);
        startActivity(intent);
    }

    public void switchToCategory() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_category);
    }

    public void switchToOrders() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_orders);
    }

    public void switchToProfile() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_profile);
    }

    // --- Data Models (Keep here as fragments reference them) ---
    public static class HomeCategory {
        public String name;
        public int iconRes;
        public int bgRes;

        public HomeCategory(String name, int iconRes, int bgRes) {
            this.name = name;
            this.iconRes = iconRes;
            this.bgRes = bgRes;
        }
    }
}
