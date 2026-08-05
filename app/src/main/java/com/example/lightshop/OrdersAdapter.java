package com.example.lightshop;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private List<OrderModel> orders;
    private boolean isAllTab;

    public OrdersAdapter(List<OrderModel> orders, boolean isAllTab) {
        this.orders = orders;
        this.isAllTab = isAllTab;
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
        holder.orderId.setText(order.getOrderId());
        holder.orderDate.setText(order.getDate());
        holder.productImage.setImageResource(order.getImageRes());
        holder.productName.setText(order.getProductName());
        holder.price.setText(order.getPrice());
        holder.qty.setText("Qty: " + order.getQuantity());

        // Special handling for the first item in "All" tab
        if (isAllTab && position == 0 && "#123456".equals(order.getOrderId())) {
            holder.statusRight.setText("Shipped");
            TextViewCompat.setCompoundDrawableTintList(holder.statusRight, ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_shipped)));
            
            holder.statusBottom.setVisibility(View.VISIBLE);
            holder.statusBottom.setText("Delivered");
            TextViewCompat.setCompoundDrawableTintList(holder.statusBottom, ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_delivered)));
        } else {
            holder.statusBottom.setVisibility(View.GONE);
            holder.statusRight.setText(order.getStatus());
            int statusColor;
            switch (order.getStatus()) {
                case "Delivered":
                    statusColor = R.color.status_delivered;
                    break;
                case "Shipped":
                    statusColor = R.color.status_shipped;
                    break;
                case "Processing":
                    statusColor = R.color.status_processing;
                    break;
                case "Cancelled":
                    statusColor = R.color.status_cancelled;
                    break;
                default:
                    statusColor = R.color.text_subtitle;
                    break;
            }
            TextViewCompat.setCompoundDrawableTintList(holder.statusRight, ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), statusColor)));
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, orderDate, productName, price, qty, statusRight, statusBottom;
        ImageView productImage;

        OrderViewHolder(View view) {
            super(view);
            orderId = view.findViewById(R.id.tv_order_id);
            orderDate = view.findViewById(R.id.tv_order_date);
            productImage = view.findViewById(R.id.iv_product);
            productName = view.findViewById(R.id.tv_product_name);
            price = view.findViewById(R.id.tv_price);
            qty = view.findViewById(R.id.tv_qty);
            statusRight = view.findViewById(R.id.tv_status_right);
            statusBottom = view.findViewById(R.id.tv_status_bottom);
        }
    }
}
