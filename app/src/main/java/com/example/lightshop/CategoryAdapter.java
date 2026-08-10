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
        public final ImageView icon;
        public final TextView name;
        public final TextView count;

        public ViewHolder(View view) {
            super(view);
            icon = view.findViewById(R.id.category_icon);
            name = view.findViewById(R.id.category_name);
            count = view.findViewById(R.id.item_count);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubCategoryModel subCategory = subCategories.get(position);
        holder.name.setText(subCategory.getSubCategoryName());
        holder.count.setVisibility(View.GONE); // Hide item count for now
        holder.icon.setImageResource(R.drawable.ic_chevron_right);
        holder.icon.setColorFilter(holder.itemView.getContext().getColor(R.color.divider_color));
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
