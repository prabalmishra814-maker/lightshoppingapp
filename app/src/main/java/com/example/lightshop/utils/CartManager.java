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

    public void addItem(ProductModel product, int quantity) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getProductId().equals(product.getProductId())) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        cartItems.add(new CartItem(product, quantity));
    }

    public void updateQuantity(int position, int delta) {
        if (position >= 0 && position < cartItems.size()) {
            CartItem item = cartItems.get(position);
            int newQty = item.getQuantity() + delta;
            if (newQty > 0) {
                item.setQuantity(newQty);
            }
        }
    }

    public void updateQuantityByProductId(String productId, int newQty) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getProductId().equals(productId)) {
                item.setQuantity(newQty);
                return;
            }
        }
    }

    public void removeItem(int position) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.remove(position);
        }
    }

    public void clear() {
        cartItems.clear();
    }

    public int getTotalMrp() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += PriceUtils.parsePrice(item.getProduct().getMrp()) * item.getQuantity();
        }
        return (int) Math.round(total);
    }

    public int getTotalSellingPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += PriceUtils.parsePrice(item.getProduct().getSellingPrice()) * item.getQuantity();
        }
        return (int) Math.round(total);
    }

    public int getTotalDiscount() {
        return getTotalMrp() - getTotalSellingPrice();
    }
}
