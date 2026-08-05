package com.example.lightshop;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

public class MyOrdersActivity extends AppCompatActivity {

    private OrdersAdapter adapter;
    private List<OrderModel> allOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_orders);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupOrdersData();
        setupRecyclerView();
        setupTabLayout();
        setupBottomNavigation();
    }

    private void setupOrdersData() {
        allOrders = new ArrayList<>();
        allOrders.add(new OrderModel("#123456", "02 May 2025", "Wireless Headphone", "₹1,299", 1, "Delivered", R.drawable.ic_headphones));
        allOrders.add(new OrderModel("#123455", "30 Apr 2025", "Men's Casual Shirt", "₹699", 1, "Shipped", R.drawable.ic_men));
        allOrders.add(new OrderModel("#123454", "28 Apr 2025", "Sneakers Shoes", "₹1,499", 1, "Processing", R.drawable.ic_shoes));
        allOrders.add(new OrderModel("#123453", "27 Apr 2025", "Smart Watch", "₹1,999", 1, "Processing", R.drawable.ic_watch));
        allOrders.add(new OrderModel("#123452", "25 Apr 2025", "Bluetooth Speaker", "₹899", 2, "Processing", R.drawable.ic_electronics));
        allOrders.add(new OrderModel("#123451", "26 Apr 2025", "Backpack", "₹749", 1, "Delivered", R.drawable.ic_backpack));
    }

    private void setupRecyclerView() {
        RecyclerView rvOrders = findViewById(R.id.rv_orders);
        adapter = new OrdersAdapter(allOrders, true);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(adapter);
    }

    private void setupTabLayout() {
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String status = tab.getText() != null ? tab.getText().toString() : "All";
                filterOrders(status);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void filterOrders(String status) {
        List<OrderModel> filteredList = new ArrayList<>();
        if ("All".equals(status)) {
            filteredList.addAll(allOrders);
        } else {
            for (OrderModel order : allOrders) {
                if (status.equals(order.getStatus())) {
                    filteredList.add(order);
                }
            }
        }
        
        RecyclerView rvOrders = findViewById(R.id.rv_orders);
        adapter = new OrdersAdapter(filteredList, "All".equals(status));
        rvOrders.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_orders);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_category) {
                startActivity(new Intent(this, CategoryActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_orders) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }
}
