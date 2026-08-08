package com.example.lightshop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lightshop.utils.CartManager;
import com.example.lightshop.utils.StatusBarUtils;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartUpdateListener {

    private RecyclerView rvCartItems;
    private CartAdapter adapter;
    private TextView tvCartTitle, tvItemsCount, tvTotalPriceMrp, tvTotalDiscount, tvTotalSellingPrice, tvSavingsMessage;
    private Button btnPlaceOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        StatusBarUtils.applyWhiteStatusBar(this);
        setContentView(R.layout.activity_cart);

        initViews();
        setupRecyclerView();
        updatePriceDetails();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        btnPlaceOrder.setOnClickListener(v -> {
            startActivity(new Intent(this, CheckoutActivity.class));
        });
    }

    private void initViews() {
        rvCartItems = findViewById(R.id.rvCartItems);
        tvCartTitle = findViewById(R.id.tvCartTitle);
        tvItemsCount = findViewById(R.id.tvItemsCount);
        tvTotalPriceMrp = findViewById(R.id.tvTotalPriceMrp);
        tvTotalDiscount = findViewById(R.id.tvTotalDiscount);
        tvTotalSellingPrice = findViewById(R.id.tvTotalSellingPrice);
        tvSavingsMessage = findViewById(R.id.tvSavingsMessage);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
    }

    private void setupRecyclerView() {
        adapter = new CartAdapter(CartManager.getInstance().getCartItems(), this);
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        rvCartItems.setAdapter(adapter);
    }

    private void updatePriceDetails() {
        CartManager manager = CartManager.getInstance();
        int count = manager.getCartItems().size();
        
        tvCartTitle.setText("My Cart (" + count + ")");
        tvItemsCount.setText("Price (" + count + " items)");
        tvTotalPriceMrp.setText("₹" + String.format("%,d", manager.getTotalMrp()));
        tvTotalDiscount.setText("-₹" + String.format("%,d", manager.getTotalDiscount()));
        tvTotalSellingPrice.setText("₹" + String.format("%,d", manager.getTotalSellingPrice()));
        tvSavingsMessage.setText("You will save ₹" + String.format("%,d", manager.getTotalDiscount()) + " on this order");
        
        btnPlaceOrder.setEnabled(count > 0);
    }

    @Override
    public void onQuantityChanged(int position, int delta) {
        CartManager.getInstance().updateQuantity(position, delta);
        adapter.notifyItemChanged(position);
        updatePriceDetails();
    }

    @Override
    public void onRemoveItem(int position) {
        CartManager.getInstance().removeItem(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, CartManager.getInstance().getCartItems().size());
        updatePriceDetails();
    }
}
