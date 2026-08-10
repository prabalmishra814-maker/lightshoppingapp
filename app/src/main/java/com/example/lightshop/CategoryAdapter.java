package com.example.lightshop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
        
        // Circular image container style is in XML. 
        // For subcategories, we usually have specific icons. 
        // Using ic_category as default if no image URL is provided in SubCategoryModel.
        holder.image.setImageResource(R.drawable.ic_category);
        holder.image.setColorFilter(holder.itemView.getContext().getColor(R.color.secondary_gray));
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
