package com.example.lightshop;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.lightshop.models.CategoryModel;
import java.util.List;

public class SidebarAdapter extends RecyclerView.Adapter<SidebarAdapter.ViewHolder> {

    private final List<CategoryModel> categories;
    private final OnItemClickListener listener;
    private int selectedPosition = 0;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public SidebarAdapter(List<CategoryModel> categories, OnItemClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View container;
        public final View selectionIndicator;
        public final View iconContainer;
        public final ImageView icon;
        public final TextView name;

        public ViewHolder(View view) {
            super(view);
            container = view.findViewById(R.id.sidebar_container);
            selectionIndicator = view.findViewById(R.id.selection_indicator);
            iconContainer = view.findViewById(R.id.icon_container);
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
        CategoryModel category = categories.get(position);
        holder.name.setText(category.getCategoryName());
        
        Glide.with(holder.itemView.getContext())
                .load(category.getCategoryImage())
                .placeholder(R.drawable.ic_category)
                .error(R.drawable.ic_category)
                .into(holder.icon);

        if (position == selectedPosition) {
            holder.container.setBackgroundColor(Color.WHITE);
            holder.selectionIndicator.setVisibility(View.VISIBLE);
            holder.name.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary_blue));
            holder.name.setTypeface(null, Typeface.BOLD);
            holder.iconContainer.setBackgroundTintList(android.content.res.ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.icon_blue_bg)));
            holder.icon.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary_blue));
        } else {
            holder.container.setBackgroundColor(Color.TRANSPARENT);
            holder.selectionIndicator.setVisibility(View.GONE);
            holder.name.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.secondary_gray));
            holder.name.setTypeface(null, Typeface.NORMAL);
            holder.iconContainer.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.WHITE));
            holder.icon.clearColorFilter();
        }

        holder.itemView.setOnClickListener(v -> {
            int oldPos = selectedPosition;
            selectedPosition = holder.getBindingAdapterPosition();
            if (oldPos != selectedPosition) {
                notifyItemChanged(oldPos);
                notifyItemChanged(selectedPosition);
                if (listener != null) {
                    listener.onItemClick(selectedPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }
}
