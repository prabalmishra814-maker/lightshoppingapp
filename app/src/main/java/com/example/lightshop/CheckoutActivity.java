package com.example.lightshop;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.lightshop.utils.CartManager;
import com.example.lightshop.utils.StatusBarUtils;

public class CheckoutActivity extends AppCompatActivity {

    private TextView tvTotalPriceMrp, tvTotalDiscount, tvTotalSellingPrice, tvSavingsMessage, tvDeliveryCharges;
    private LinearLayout optionFree, optionExpress;
    private ImageView ivRadioFree, ivRadioExpress;
    private boolean isExpress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        StatusBarUtils.applyWhiteStatusBar(this);
        setContentView(R.layout.activity_checkout);

        initViews();
        updatePriceDetails();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        optionFree.setOnClickListener(v -> selectDelivery(false));
        optionExpress.setOnClickListener(v -> selectDelivery(true));
    }

    private void initViews() {
        tvTotalPriceMrp = findViewById(R.id.tvTotalPriceMrp);
        tvTotalDiscount = findViewById(R.id.tvTotalDiscount);
        tvTotalSellingPrice = findViewById(R.id.tvTotalSellingPrice);
        tvSavingsMessage = findViewById(R.id.tvSavingsMessage);
        tvDeliveryCharges = findViewById(R.id.tvDeliveryCharges);
        
        optionFree = findViewById(R.id.optionFree);
        optionExpress = findViewById(R.id.optionExpress);
        ivRadioFree = findViewById(R.id.ivRadioFree);
        ivRadioExpress = findViewById(R.id.ivRadioExpress);
    }

    private void selectDelivery(boolean express) {
        isExpress = express;
        ivRadioFree.setImageResource(express ? R.drawable.ic_radio_unselected : R.drawable.ic_radio_selected);
        ivRadioExpress.setImageResource(express ? R.drawable.ic_radio_selected : R.drawable.ic_radio_unselected);
        updatePriceDetails();
    }

    private void updatePriceDetails() {
        CartManager manager = CartManager.getInstance();
        int deliveryFee = isExpress ? 40 : 0;
        int totalSelling = manager.getTotalSellingPrice() + deliveryFee;

        tvTotalPriceMrp.setText("₹" + String.format("%,d", manager.getTotalMrp()));
        tvTotalDiscount.setText("-₹" + String.format("%,d", manager.getTotalDiscount()));
        tvDeliveryCharges.setText(deliveryFee == 0 ? "FREE" : "₹" + deliveryFee);
        tvDeliveryCharges.setTextColor(getResources().getColor(deliveryFee == 0 ? R.color.savings_green : R.color.text_primary));
        
        tvTotalSellingPrice.setText("₹" + String.format("%,d", totalSelling));
        tvSavingsMessage.setText("You will save ₹" + String.format("%,d", manager.getTotalDiscount()) + " on this order");
    }
}
