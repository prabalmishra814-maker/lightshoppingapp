package com.example.lightshop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lightshop.models.CartItem;
import com.example.lightshop.models.ProductModel;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private OnCartUpdateListener listener;

    public interface OnCartUpdateListener {
        void onQuantityChanged(int position, int delta);
        void onRemoveItem(int position);
    }

    public CartAdapter(List<CartItem> cartItems, OnCartUpdateListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_product, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        ProductModel product = cartItem.getProduct();

        holder.tvProductName.setText(product.getProductName());
        holder.tvProductDesc.setText(product.getShortDescription());
        holder.tvStockStatus.setText(product.getStock());
        holder.tvSellingPrice.setText("₹" + product.getSellingPrice());
        holder.tvMrp.setText("₹" + product.getMrp());
        holder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));

        // Calculate discount %
        try {
            int mrp = Integer.parseInt(product.getMrp());
            int selling = Integer.parseInt(product.getSellingPrice());
            int discount = ((mrp - selling) * 100) / mrp;
            holder.tvDiscount.setText(discount + "% off");
        } catch (Exception e) {
            holder.tvDiscount.setText("0% off");
        }

        // Set Image (Hardcoded for demo as per reference or use existing)
        if (product.getProductName().contains("boAt")) {
            holder.ivProductImage.setImageResource(R.drawable.ic_headphones);
        } else if (product.getProductName().contains("Noise")) {
            holder.ivProductImage.setImageResource(R.drawable.ic_watch);
        }

        holder.btnPlus.setOnClickListener(v -> listener.onQuantityChanged(position, 1));
        holder.btnMinus.setOnClickListener(v -> listener.onQuantityChanged(position, -1));
        holder.btnRemove.setOnClickListener(v -> listener.onRemoveItem(position));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage, btnMinus, btnPlus;
        TextView tvProductName, tvProductDesc, tvStockStatus, tvSellingPrice, tvMrp, tvDiscount, tvQuantity, btnRemove, btnSaveForLater;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductDesc = itemView.findViewById(R.id.tvProductDesc);
            tvStockStatus = itemView.findViewById(R.id.tvStockStatus);
            tvSellingPrice = itemView.findViewById(R.id.tvSellingPrice);
            tvMrp = itemView.findViewById(R.id.tvMrp);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            btnSaveForLater = itemView.findViewById(R.id.btnSaveForLater);
        }
    }
}
