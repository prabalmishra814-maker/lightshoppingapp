package com.example.lightshop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

public class MyOrdersFragment extends Fragment {

    private OrdersAdapter adapter;
    private List<OrderModel> allOrders;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupOrdersData();
        setupRecyclerView(view);
        setupTabLayout(view);
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

    private void setupRecyclerView(View view) {
        RecyclerView rvOrders = view.findViewById(R.id.rv_orders);
        adapter = new OrdersAdapter(allOrders, true);
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOrders.setAdapter(adapter);
    }

    private void setupTabLayout(View view) {
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String status = tab.getText() != null ? tab.getText().toString() : "All";
                filterOrders(view, status);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void filterOrders(View view, String status) {
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
        
        RecyclerView rvOrders = view.findViewById(R.id.rv_orders);
        adapter = new OrdersAdapter(filteredList, "All".equals(status));
        rvOrders.setAdapter(adapter);
    }
}
