package com.amstudio.lightbasket;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.amstudio.lightbasket.api.SupabaseClient;
import com.amstudio.lightbasket.api.SessionManager;
import com.amstudio.lightbasket.models.ProductModel;
import com.amstudio.lightbasket.models.WishlistModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.ViewHolder> {

    private final List<ProductModel> items;
    private final Context context;
    private final SessionManager sessionManager;
    private final Set<String> wishlistProductIds;

    public RecommendationAdapter(Context context, List<ProductModel> items, Set<String> wishlistProductIds) {
        this.context = context;
        this.items = items;
        this.wishlistProductIds = wishlistProductIds;
        this.sessionManager = new SessionManager(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, mrp, discount, rating;
        ImageView image, wishlist;
        View ratingContainer;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tv_product_name);
            price = view.findViewById(R.id.tv_price);
            mrp = view.findViewById(R.id.tv_mrp);
            discount = view.findViewById(R.id.tv_discount);
            
            image = view.findViewById(R.id.iv_product);
            wishlist = view.findViewById(R.id.iv_wishlist);
            rating = view.findViewById(R.id.tv_rating);
            ratingContainer = view.findViewById(R.id.rating_container);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_recommendation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel item = items.get(position);
        holder.name.setText(item.getProductName());

        // Bind Rating
        if (item.getRating() != null && !item.getRating().isEmpty() && !item.getRating().equals("0")) {
            holder.rating.setText(item.getRating());
            holder.ratingContainer.setVisibility(View.VISIBLE);
        } else {
            holder.ratingContainer.setVisibility(View.GONE);
        }

        String sellingPriceStr = item.getSellingPrice() != null ? item.getSellingPrice() : item.getPrice();
        String mrpStr = item.getMrp() != null ? item.getMrp() : item.getMainPrice();

        try {
            String sClean = sellingPriceStr != null ? sellingPriceStr.replaceAll("[^0-9.]", "") : "0";
            String mClean = mrpStr != null ? mrpStr.replaceAll("[^0-9.]", "") : "0";

            double sPrice = Double.parseDouble(sClean);
            double mPrice = Double.parseDouble(mClean);

            holder.price.setText("₹" + (int) sPrice);
            holder.mrp.setText("₹" + (int) mPrice);
            holder.mrp.setPaintFlags(holder.mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            if (mPrice > sPrice && mPrice > 0) {
                int disc = (int) Math.round(((mPrice - sPrice) / mPrice) * 100);
                holder.discount.setText(disc + "% OFF");
                holder.discount.setVisibility(View.VISIBLE);
            } else {
                holder.discount.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            holder.price.setText("₹" + sellingPriceStr);
            holder.mrp.setText("₹" + mrpStr);
        }

        loadImage(item.getProductImage(), holder.image);

        if (wishlistProductIds.contains(item.getProductId())) {
            holder.wishlist.setImageResource(R.drawable.ic_heart_filled);
            holder.wishlist.setColorFilter(context.getResources().getColor(R.color.color_logout));
        } else {
            holder.wishlist.setImageResource(R.drawable.ic_heart_outline);
            holder.wishlist.setColorFilter(context.getResources().getColor(R.color.dark_navy));
        }

        holder.wishlist.setOnClickListener(v -> {
            if (wishlistProductIds.contains(item.getProductId())) {
                removeFromWishlist(item);
            } else {
                addToWishlist(item);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product", item);
            context.startActivity(intent);
        });
    }

    private void loadImage(String imageSource, ImageView imageView) {
        if (imageSource == null || imageSource.isEmpty()) {
            imageView.setImageResource(R.drawable.ic_headphones);
            return;
        }

        if (imageSource.startsWith("http")) {
            Glide.with(context)
                    .load(imageSource)
                    .placeholder(R.drawable.ic_headphones)
                    .error(R.drawable.ic_headphones)
                    .into(imageView);
        } else {
            int resId = context.getResources().getIdentifier(imageSource, "drawable", context.getPackageName());
            if (resId != 0) {
                Glide.with(context)
                        .load(resId)
                        .placeholder(R.drawable.ic_headphones)
                        .into(imageView);
            } else {
                Glide.with(context)
                        .load(imageSource)
                        .placeholder(R.drawable.ic_headphones)
                        .error(R.drawable.ic_headphones)
                        .into(imageView);
            }
        }
    }

    private void addToWishlist(ProductModel product) {
        String userId = sessionManager.getUserId();
        if (userId.isEmpty()) {
            Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("user_id", userId);
        data.put("product_id", product.getProductId());
        data.put("product_name", product.getProductName());
        data.put("product_price", product.getSellingPrice() != null ? product.getSellingPrice() : product.getPrice());
        data.put("product_image", product.getProductImage());

        String authHeader = "Bearer " + sessionManager.getToken();
        SupabaseClient.getApiService().addData(
                "wishlist",
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "return=representation",
                data
        ).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(@NonNull Call<List<Map<String, Object>>> call, @NonNull Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful()) {
                    wishlistProductIds.add(product.getProductId());
                    notifyDataSetChanged();
                    Toast.makeText(context, "Added to Wishlist", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Map<String, Object>>> call, @NonNull Throwable t) {}
        });
    }

    private void removeFromWishlist(ProductModel product) {
        String userId = sessionManager.getUserId();
        if (userId.isEmpty()) return;

        String authHeader = "Bearer " + sessionManager.getToken();
        Map<String, String> filters = new HashMap<>();
        filters.put("user_id", "eq." + userId);
        filters.put("product_id", "eq." + product.getProductId());

        SupabaseClient.getApiService().deleteDataByFilters(
                "wishlist",
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                filters
        ).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    wishlistProductIds.remove(product.getProductId());
                    notifyDataSetChanged();
                    Toast.makeText(context, "Removed from Wishlist", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {}
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

