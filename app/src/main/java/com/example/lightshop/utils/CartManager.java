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
        // Pre-populate with sample data as per reference screenshot
        addSampleData();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    private void addSampleData() {
        ProductModel p1 = new ProductModel();
        p1.setProductName("boAt Airdopes 141 Pro");
        p1.setShortDescription("Bluetooth Earbuds (Black)");
        p1.setSellingPrice("1299");
        p1.setMrp("1999");
        p1.setStock("In Stock");
        cartItems.add(new CartItem(p1, 1));

        ProductModel p2 = new ProductModel();
        p2.setProductName("Noise ColorFit Pulse 2");
        p2.setShortDescription("Max Smartwatch (Jet Black)");
        p2.setSellingPrice("1799");
        p2.setMrp("2499");
        p2.setStock("In Stock");
        cartItems.add(new CartItem(p2, 1));
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
                total += Integer.parseInt(item.getProduct().getMrp()) * item.getQuantity();
            } catch (Exception ignored) {}
        }
        return total;
    }

    public int getTotalSellingPrice() {
        int total = 0;
        for (CartItem item : cartItems) {
            try {
                total += Integer.parseInt(item.getProduct().getSellingPrice()) * item.getQuantity();
            } catch (Exception ignored) {}
        }
        return total;
    }

    public int getTotalDiscount() {
        return getTotalMrp() - getTotalSellingPrice();
    }
}
