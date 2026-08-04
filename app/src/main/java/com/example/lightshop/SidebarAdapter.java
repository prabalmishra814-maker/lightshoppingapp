package com.example.lightshop;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SidebarAdapter extends RecyclerView.Adapter<SidebarAdapter.ViewHolder> {

    private final List<Category> categories;
    private final OnItemClickListener listener;
    private int selectedPosition = 0;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public SidebarAdapter(List<Category> categories, OnItemClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View container;
        public final ImageView icon;
        public final TextView name;

        public ViewHolder(View view) {
            super(view);
            container = view.findViewById(R.id.sidebar_container);
            icon = view.findViewById(R.id.sidebar_icon);
            name = view.findViewById(R.id.sidebar_name);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sidebar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.name.setText(category.getName());
        holder.icon.setImageResource(category.getIconRes());

        if (position == selectedPosition) {
            holder.container.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.selected_bg));
            holder.name.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary));
            holder.icon.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary));
        } else {
            holder.container.setBackgroundColor(Color.TRANSPARENT);
            holder.name.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_subtitle));
            holder.icon.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_hint));
        }

        holder.itemView.setOnClickListener(v -> {
            int oldPos = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(oldPos);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onItemClick(selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }
}
