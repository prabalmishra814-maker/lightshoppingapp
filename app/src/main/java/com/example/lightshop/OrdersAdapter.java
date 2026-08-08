package com.example.lightshop;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
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
        holder.tvOrderId.setText(order.getOrderId());
        holder.tvOrderDate.setText(order.getDate());
        holder.ivProduct.setImageResource(order.getImageRes());
        holder.tvProductName.setText(order.getProductName());
        holder.tvPrice.setText(order.getPrice());
        holder.tvQty.setText("Qty: " + order.getQuantity());
        holder.tvStatus.setText(order.getStatus());

        int statusColor;
        switch (order.getStatus()) {
            case "Delivered":
                statusColor = R.color.status_delivered;
                holder.btnSecondary.setVisibility(View.VISIBLE);
                holder.btnSecondary.setText("Buy Again");
                break;
            case "Shipped":
                statusColor = R.color.status_shipped;
                holder.btnSecondary.setVisibility(View.VISIBLE);
                holder.btnSecondary.setText("Track Order");
                break;
            case "Processing":
                statusColor = R.color.status_processing;
                holder.btnSecondary.setVisibility(View.GONE);
                break;
            case "Cancelled":
                statusColor = R.color.status_cancelled;
                holder.btnSecondary.setVisibility(View.GONE);
                break;
            default:
                statusColor = R.color.text_subtitle;
                holder.btnSecondary.setVisibility(View.GONE);
                break;
        }

        holder.vStatusDot.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), statusColor)));

        holder.btnSecondary.setOnClickListener(v -> {
            if (order.getStatus().equals("Delivered")) {
                listener.onBuyAgainClick(order);
            }
        });

        holder.btnViewDetails.setOnClickListener(v -> listener.onViewDetailsClick(order));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvProductName, tvPrice, tvQty, tvStatus;
        ImageView ivProduct;
        View vStatusDot;
        MaterialButton btnSecondary, btnViewDetails;

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
            btnSecondary = view.findViewById(R.id.btn_secondary);
            btnViewDetails = view.findViewById(R.id.btn_view_details);
        }
    }
}
