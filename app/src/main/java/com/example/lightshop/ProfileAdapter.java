package com.example.lightshop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.MenuViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(ProfileMenuModel item);
    }

    private List<ProfileMenuModel> menuItems;
    private OnItemClickListener listener;

    public ProfileAdapter(List<ProfileMenuModel> menuItems, OnItemClickListener listener) {
        this.menuItems = menuItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        ProfileMenuModel item = menuItems.get(position);
        holder.tvMenuTitle.setText(item.getTitle());
        holder.tvMenuTitle.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), item.getTextColorResId()));
        
        holder.ivMenuIcon.setImageResource(item.getIconResId());
        holder.ivMenuIcon.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), item.getIconColorResId()));
        
        holder.ivChevron.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), item.getIconColorResId()));

        // Hide divider for last item
        holder.divider.setVisibility(position == menuItems.size() - 1 ? View.GONE : View.VISIBLE);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMenuIcon, ivChevron;
        TextView tvMenuTitle;
        View divider;

        MenuViewHolder(View view) {
            super(view);
            ivMenuIcon = view.findViewById(R.id.ivMenuIcon);
            tvMenuTitle = view.findViewById(R.id.tvMenuTitle);
            ivChevron = view.findViewById(R.id.ivChevron);
            divider = view.findViewById(R.id.divider);
        }
    }
}
