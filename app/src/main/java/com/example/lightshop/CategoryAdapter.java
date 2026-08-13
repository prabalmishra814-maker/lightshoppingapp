package com.example.lightshop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.lightshop.models.SubCategoryModel;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<SubCategoryModel> subCategories;

    public CategoryAdapter(List<SubCategoryModel> subCategories) {
        this.subCategories = subCategories;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView image;
        public final TextView name;

        public ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.subcategory_image);
            name = view.findViewById(R.id.subcategory_name);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subcategory_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubCategoryModel subCategory = subCategories.get(position);
        holder.name.setText(subCategory.getSubCategoryName());
        
        String imageUrl = subCategory.getSubCategoryImage();
        
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (imageUrl.startsWith("res:")) {
                try {
                    int resId = Integer.parseInt(imageUrl.substring(4));
                    holder.image.setImageResource(resId);
                    holder.image.clearColorFilter();
                } catch (Exception e) {
                    holder.image.setImageResource(R.drawable.ic_category);
                    holder.image.setColorFilter(holder.itemView.getContext().getColor(R.color.secondary_gray));
                }
            } else {
                Glide.with(holder.itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_category)
                        .error(R.drawable.ic_category)
                        .into(holder.image);
                holder.image.clearColorFilter();
            }
        } else {
            // Fallback to local icons if image is null
            int iconRes = R.drawable.ic_category;
            String name = subCategory.getSubCategoryName().toLowerCase();
            if (name.contains("laptop")) iconRes = R.drawable.ic_electronics;
            else if (name.contains("mobile")) iconRes = R.drawable.ic_electronics;
            else if (name.contains("watch")) iconRes = R.drawable.ic_watch;
            else if (name.contains("shoe")) iconRes = R.drawable.ic_shoes;
            else if (name.contains("shirt")) iconRes = R.drawable.ic_men;
            else if (name.contains("beauty")) iconRes = R.drawable.ic_beauty;
            else if (name.contains("home")) iconRes = R.drawable.ic_home_cat;
            else if (name.contains("kid")) iconRes = R.drawable.ic_kids;
            
            holder.image.setImageResource(iconRes);
            if (iconRes == R.drawable.ic_category) {
                holder.image.setColorFilter(holder.itemView.getContext().getColor(R.color.secondary_gray));
            } else {
                holder.image.clearColorFilter();
            }
        }

        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(), ProductListActivity.class);
            intent.putExtra("subcategory", subCategory.getSubCategoryName());
            intent.putExtra("title", subCategory.getSubCategoryName());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return subCategories != null ? subCategories.size() : 0;
    }

    public void updateData(List<SubCategoryModel> newSubCategories) {
        this.subCategories = newSubCategories;
        notifyDataSetChanged();
    }
}
