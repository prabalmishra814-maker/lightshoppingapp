package com.amstudio.lightbasket;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.amstudio.lightbasket.models.CartItem;
import com.amstudio.lightbasket.models.ProductModel;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private OnCartUpdateListener listener;

    public interface OnCartUpdateListener {
        void onQuantityChanged(int position, int delta);
        void onRemoveItem(int position);
        void onMoveToWishlist(int position);
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

        holder.tvProductName.setText(product.getProductName() != null ? product.getProductName() : "Product");
        holder.tvProductDesc.setText(product.getShortDescription() != null ? product.getShortDescription() : "");
        holder.tvStockStatus.setText(product.getStock() != null ? product.getStock() : "In Stock");
        
        double sellingPrice = com.amstudio.lightbasket.utils.PriceUtils.parsePrice(product.getSellingPrice());
        double mrp = com.amstudio.lightbasket.utils.PriceUtils.parsePrice(product.getMrp());
        
        holder.tvSellingPrice.setText(com.amstudio.lightbasket.utils.PriceUtils.formatPrice(sellingPrice));
        holder.tvMrp.setText(com.amstudio.lightbasket.utils.PriceUtils.formatPrice(mrp));
        holder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));

        if (product.getSize() != null && !product.getSize().isEmpty() && !product.getSize().equals("0") && !product.getSize().equals("null")) {
            holder.tvSelectedSize.setVisibility(View.VISIBLE);
            holder.tvSelectedSize.setText("Size: " + product.getSize());
        } else {
            holder.tvSelectedSize.setVisibility(View.GONE);
        }

        // Load image using Glide
        Glide.with(holder.itemView.getContext())
                .load(product.getProductImage())
                .placeholder(R.drawable.ic_headphones)
                .error(R.drawable.ic_headphones)
                .into(holder.ivProductImage);

        // Calculate discount %
        if (mrp > 0) {
            int discount = (int) (((mrp - sellingPrice) * 100) / mrp);
            holder.tvDiscount.setText(discount + "% off");
        } else {
            holder.tvDiscount.setText("0% off");
        }

        holder.btnPlus.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                listener.onQuantityChanged(pos, 1);
            }
        });

        holder.btnMinus.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                listener.onQuantityChanged(pos, -1);
            }
        });

        holder.btnRemove.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                listener.onRemoveItem(pos);
            }
        });

        holder.btnMoveToWishlist.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                listener.onMoveToWishlist(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage, btnMinus, btnPlus;
        TextView tvProductName, tvProductDesc, tvStockStatus, tvSellingPrice, tvMrp, tvDiscount, tvQuantity, btnRemove, btnMoveToWishlist, tvSelectedSize;

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
            btnMoveToWishlist = itemView.findViewById(R.id.btnMoveToWishlist);
            tvSelectedSize = itemView.findViewById(R.id.tvSelectedSize);
        }
    }
}

