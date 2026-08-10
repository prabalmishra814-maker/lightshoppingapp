package com.example.lightshop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lightshop.models.ProductModel;
import com.google.android.material.button.MaterialButton;
import com.bumptech.glide.Glide;
import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    private List<ProductModel> wishlistItems;
    private OnWishlistInteractionListener listener;

    public interface OnWishlistInteractionListener {
        void onRemoveItem(int position);
        void onAddToCart(int position);
        void onItemClick(ProductModel product);
    }

    public WishlistAdapter(List<ProductModel> wishlistItems, OnWishlistInteractionListener listener) {
        this.wishlistItems = wishlistItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wishlist, parent, false);
        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
        ProductModel product = wishlistItems.get(position);

        holder.tvProductName.setText(product.getProductName());
        holder.tvBrand.setText(product.getBrand());
        
        String sellingPriceStr = product.getSellingPrice();
        String mrpStr = product.getMrp();
        if (sellingPriceStr == null) sellingPriceStr = product.getPrice();
        if (mrpStr == null) mrpStr = product.getMainPrice();

        try {
            String sClean = sellingPriceStr != null ? sellingPriceStr.replaceAll("[^0-9.]", "") : "0";
            String mClean = mrpStr != null ? mrpStr.replaceAll("[^0-9.]", "") : "0";

            double sPrice = Double.parseDouble(sClean);
            double mPrice = Double.parseDouble(mClean);
            
            holder.tvPrice.setText("₹" + (int) sPrice);
            holder.tvMrp.setText("₹" + (int) mPrice);
            holder.tvMrp.setPaintFlags(holder.tvMrp.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
        } catch (Exception e) {
            holder.tvPrice.setText("₹" + (sellingPriceStr != null ? sellingPriceStr : "0"));
            holder.tvMrp.setText("₹" + (mrpStr != null ? mrpStr : "0"));
        }

        Glide.with(holder.itemView.getContext())
                .load(product.getProductImage())
                .placeholder(R.drawable.ic_category)
                .error(R.drawable.ic_category)
                .into(holder.ivProduct);

        holder.btnRemove.setOnClickListener(v -> listener.onRemoveItem(position));
        holder.btnAddToCart.setOnClickListener(v -> listener.onAddToCart(position));
        holder.itemView.setOnClickListener(v -> listener.onItemClick(product));
    }

    @Override
    public int getItemCount() {
        return wishlistItems.size();
    }

    public static class WishlistViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct, btnRemove;
        TextView tvProductName, tvBrand, tvPrice, tvMrp;
        MaterialButton btnAddToCart;

        public WishlistViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvBrand = itemView.findViewById(R.id.tvBrand);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvMrp = itemView.findViewById(R.id.tvMrp);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}
