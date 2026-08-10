package com.example.lightshop.utils;

import com.example.lightshop.models.CartItem;
import com.example.lightshop.models.ProductModel;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<CartItem> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
        // No sample data, fetch from DB
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void setCartItems(List<CartItem> items) {
        this.cartItems = items;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void updateQuantity(int position, int delta) {
        CartItem item = cartItems.get(position);
        int newQty = item.getQuantity() + delta;
        if (newQty > 0) {
            item.setQuantity(newQty);
        }
    }

    public void removeItem(int position) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.remove(position);
        }
    }

    public int getTotalMrp() {
        int total = 0;
        for (CartItem item : cartItems) {
            try {
                String mrpStr = item.getProduct().getMrp();
                if (mrpStr == null) mrpStr = "0";
                String clean = mrpStr.replaceAll("[^0-9.]", "");
                if (!clean.isEmpty()) {
                    total += (int) Double.parseDouble(clean) * item.getQuantity();
                }
            } catch (Exception ignored) {}
        }
        return total;
    }

    public int getTotalSellingPrice() {
        int total = 0;
        for (CartItem item : cartItems) {
            try {
                String priceStr = item.getProduct().getSellingPrice();
                if (priceStr == null) priceStr = "0";
                String clean = priceStr.replaceAll("[^0-9.]", "");
                if (!clean.isEmpty()) {
                    total += (int) Double.parseDouble(clean) * item.getQuantity();
                }
            } catch (Exception ignored) {}
        }
        return total;
    }

    public int getTotalDiscount() {
        return getTotalMrp() - getTotalSellingPrice();
    }
}
