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

import android.content.Intent;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

public class MyOrdersFragment extends Fragment implements OrdersAdapter.OnOrderClickListener {

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
    }

    private void setupRecyclerView(View view) {
        RecyclerView rvOrders = view.findViewById(R.id.rv_orders);
        adapter = new OrdersAdapter(allOrders, this);
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOrders.setAdapter(adapter);
    }

    @Override
    public void onBuyAgainClick(OrderModel order) {
        // Navigate to CheckoutActivity (Order placement page)
        Intent intent = new Intent(getContext(), CheckoutActivity.class);
        startActivity(intent);
    }

    @Override
    public void onViewDetailsClick(OrderModel order) {
        // Implement View Details logic if needed
    }

    private void setupTabLayout(View view) {
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String status = tab.getText() != null ? tab.getText().toString() : "ALL";
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
        if ("ALL".equalsIgnoreCase(status)) {
            filteredList.addAll(allOrders);
        } else {
            for (OrderModel order : allOrders) {
                if (status.equalsIgnoreCase(order.getStatus())) {
                    filteredList.add(order);
                }
            }
        }
        adapter.updateData(filteredList);
    }
}
