package com.amstudio.lightbasket;

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
import android.widget.Toast;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.amstudio.lightbasket.api.SessionManager;
import com.amstudio.lightbasket.api.SupabaseClient;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrdersFragment extends Fragment implements OrdersAdapter.OnOrderClickListener {

    private OrdersAdapter adapter;
    private List<OrderModel> allOrders = new ArrayList<>();
    private SessionManager sessionManager;
    private SwipeRefreshLayout swipeRefresh;
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(requireContext());
        
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setOnRefreshListener(this::fetchOrdersFromSupabase);
        
        tabLayout = view.findViewById(R.id.tab_layout);
        
        setupRecyclerView(view);
        setupTabLayout();
        fetchOrdersFromSupabase();
    }

    private void fetchOrdersFromSupabase() {
        String userId = sessionManager.getUserId();
        if (userId.isEmpty()) return;

        String authHeader = "Bearer " + sessionManager.getToken();
        
        Map<String, String> filters = new HashMap<>();
        filters.put("select", "*");
        filters.put("user_id", "eq." + userId);
        filters.put("order", "created_at.desc");

        SupabaseClient.getApiService().fetchDataWithFilters(
                "orders",
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                filters
        ).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    allOrders.clear();
                    for (Map<String, Object> orderMap : response.body()) {
                        parseOrder(orderMap);
                    }
                    
                    // Re-apply filter for current selected tab
                    int selectedTabPos = tabLayout.getSelectedTabPosition();
                    TabLayout.Tab selectedTab = tabLayout.getTabAt(selectedTabPos);
                    String filter = (selectedTab != null && selectedTab.getText() != null) 
                            ? selectedTab.getText().toString() : "ALL";
                    filterOrders(filter);

                } else {
                    Toast.makeText(getContext(), "Something went wrong. Please contact the developer.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Something went wrong. Please contact the developer.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void parseOrder(Map<String, Object> orderMap) {
        try {
            String orderId = String.valueOf(orderMap.get("id"));
            String orderNumber = String.valueOf(orderMap.get("order_number"));
            String createdAt = String.valueOf(orderMap.get("created_at"));
            String customerName = String.valueOf(orderMap.get("customer_name"));
            String customerPhone = String.valueOf(orderMap.get("customer_phone"));
            String paymentMethod = String.valueOf(orderMap.get("payment_method"));
            String finalAmount = String.valueOf(orderMap.get("final_amount"));
            String replacementReason = String.valueOf(orderMap.get("replacement_reason"));
            
            // USE MAIN ORDER STATUS from the column you showed in screenshot
            String mainStatus = String.valueOf(orderMap.get("order_status"));
            
            String dateDisplay = createdAt != null && createdAt.contains("T") ? createdAt.split("T")[0] : createdAt;

            // Extract Address & GPS
            String fullAddress = "";
            double lat = 0.0, lng = 0.0;
            Object addrObj = orderMap.get("delivery_address");
            if (addrObj instanceof Map) {
                Map<String, Object> addrMap = (Map<String, Object>) addrObj;
                fullAddress = String.valueOf(addrMap.get("full_address"));
                Object latObj = addrMap.get("latitude");
                Object lngObj = addrMap.get("longitude");
                if (latObj instanceof Number) lat = ((Number) latObj).doubleValue();
                if (lngObj instanceof Number) lng = ((Number) lngObj).doubleValue();
            }

            Object itemsObj = orderMap.get("order_items");
            if (itemsObj instanceof List) {
                List<Map<String, Object>> items = (List<Map<String, Object>>) itemsObj;
                for (Map<String, Object> item : items) {
                    String pName = String.valueOf(item.get("product_name"));
                    String price = String.valueOf(item.get("price_per_unit"));
                    
                    int qty = 1;
                    Object qtyObj = item.get("quantity");
                    if (qtyObj instanceof Number) {
                        qty = ((Number) qtyObj).intValue();
                    }
                    
                    // Fallback: use mainStatus if item_status is Pending or null
                    String itemStatus = String.valueOf(item.get("item_status"));
                    String finalStatus = (itemStatus == null || itemStatus.equalsIgnoreCase("Pending")) ? mainStatus : itemStatus;
                    
                    String imageUrl = String.valueOf(item.get("product_image"));
                    String pSize = item.containsKey("product_size") ? String.valueOf(item.get("product_size")) : "";

                    allOrders.add(new OrderModel(orderId, orderNumber, dateDisplay, pName, price, qty, finalStatus, imageUrl, customerName, customerPhone, fullAddress, lat, lng, paymentMethod, finalAmount, replacementReason, pSize));
                }
            }
        } catch (Exception e) {
            android.util.Log.e("MyOrdersFragment", "Error parsing order", e);
        }
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

    private void setupTabLayout() {
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

    private void filterOrders(String filterStatus) {
        List<OrderModel> filteredList = new ArrayList<>();
        if ("ALL".equalsIgnoreCase(filterStatus)) {
            filteredList.addAll(allOrders);
        } else {
            for (OrderModel order : allOrders) {
                String orderStatus = order.getStatus();
                if (orderStatus == null) continue;

                // Exact match based on user's simplified tabs
                if (orderStatus.equalsIgnoreCase(filterStatus)) {
                    filteredList.add(order);
                } else if (filterStatus.equalsIgnoreCase("CANCELLED") && 
                          (orderStatus.equalsIgnoreCase("Cancelled") || orderStatus.equalsIgnoreCase("Out of Stock"))) {
                    filteredList.add(order);
                } else if (filterStatus.equalsIgnoreCase("CONFIRMED") && 
                          (orderStatus.equalsIgnoreCase("Confirmed") || orderStatus.equalsIgnoreCase("Ready to Ship"))) {
                    filteredList.add(order);
                }
            }
        }
        adapter.updateData(filteredList);
    }
}

