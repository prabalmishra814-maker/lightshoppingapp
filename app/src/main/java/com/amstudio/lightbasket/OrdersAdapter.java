package com.amstudio.lightbasket;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    public interface OnOrderClickListener {
        void onBuyAgainClick(OrderModel order);
        void onViewDetailsClick(OrderModel order);
    }

    private List<OrderModel> orders;
    private OnOrderClickListener listener;

    public OrdersAdapter(List<OrderModel> orders, OnOrderClickListener listener) {
        this.orders = orders;
        this.listener = listener;
    }

    public void updateData(List<OrderModel> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel order = orders.get(position);

        holder.tvOrderId.setText(order.getOrderNumber() != null ? order.getOrderNumber() : order.getOrderId());
        holder.tvOrderDate.setText(order.getDate());
        
        Glide.with(holder.itemView.getContext())
                .load(order.getImageUrl())
                .placeholder(R.drawable.ic_headphones)
                .into(holder.ivProduct);

        holder.tvProductName.setText(order.getProductName());
        holder.tvPrice.setText("₹" + order.getPrice());
        holder.tvQty.setText("Qty: " + order.getQuantity());
        holder.tvStatus.setText(order.getStatus());

        String status = order.getStatus() != null ? order.getStatus() : "Pending";
        int statusColor;
        
        if (status.equalsIgnoreCase("Delivered")) {
            statusColor = R.color.status_delivered;
        } else if (status.equalsIgnoreCase("Shipped")) {
            statusColor = R.color.accent_blue;
        } else if (status.equalsIgnoreCase("Confirmed") || status.equalsIgnoreCase("Ready to Ship")) {
            statusColor = R.color.accent_blue;
        } else if (status.equalsIgnoreCase("Cancelled") || status.equalsIgnoreCase("Out of Stock")) {
            statusColor = R.color.status_cancelled;
        } else {
            statusColor = R.color.text_subtitle;
        }

        holder.vStatusDot.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), statusColor)));

        holder.btnViewDetails.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), OrderDetailsActivity.class);
            intent.putExtra("order_id", order.getOrderId());
            intent.putExtra("order_number", order.getOrderNumber());
            intent.putExtra("customer_name", order.getCustomerName());
            intent.putExtra("customer_phone", order.getCustomerPhone());
            intent.putExtra("full_address", order.getFullAddress());
            intent.putExtra("product_name", order.getProductName());
            intent.putExtra("price", order.getPrice());
            intent.putExtra("quantity", order.getQuantity());
            intent.putExtra("status", order.getStatus());
            intent.putExtra("image_url", order.getImageUrl());
            intent.putExtra("latitude", order.getLatitude());
            intent.putExtra("longitude", order.getLongitude());
            intent.putExtra("payment_method", order.getPaymentMethod());
            intent.putExtra("final_amount", order.getFinalAmount());
            intent.putExtra("replacement_reason", order.getReplacementReason());
            intent.putExtra("product_size", order.getProductSize());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvProductName, tvPrice, tvQty, tvStatus;
        ImageView ivProduct;
        View vStatusDot;
        MaterialButton btnViewDetails;

        OrderViewHolder(View view) {
            super(view);
            tvOrderId = view.findViewById(R.id.tv_order_id);
            tvOrderDate = view.findViewById(R.id.tv_order_date);
            ivProduct = view.findViewById(R.id.iv_product);
            tvProductName = view.findViewById(R.id.tv_product_name);
            tvPrice = view.findViewById(R.id.tv_price);
            tvQty = view.findViewById(R.id.tv_qty);
            tvStatus = view.findViewById(R.id.tv_status);
            vStatusDot = view.findViewById(R.id.v_status_dot);
            btnViewDetails = view.findViewById(R.id.btn_view_details);
        }
    }
}

