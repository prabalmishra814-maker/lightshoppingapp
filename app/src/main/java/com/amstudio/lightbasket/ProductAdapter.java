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
import com.amstudio.lightbasket.api.SessionManager;
import com.amstudio.lightbasket.api.SupabaseClient;
import com.amstudio.lightbasket.models.ProductModel;
import com.amstudio.lightbasket.utils.CartHelper;
import com.amstudio.lightbasket.utils.CartManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private final List<ProductModel> items;
    private final boolean isHorizontal;
    private final Set<String> cartProductIds;
    private final Set<String> wishlistProductIds;
    private final SessionManager sessionManager;
    private final Context context;
    private final UpdateCallback callback;

    public interface UpdateCallback {
        void onUpdate();
    }

    public ProductAdapter(Context context, List<ProductModel> items, boolean isHorizontal, 
                          Set<String> cartProductIds, Set<String> wishlistProductIds, 
                          SessionManager sessionManager, UpdateCallback callback) {
        this.context = context;
        this.items = items;
        this.isHorizontal = isHorizontal;
        this.cartProductIds = cartProductIds;
        this.wishlistProductIds = wishlistProductIds;
        this.sessionManager = sessionManager;
        this.callback = callback;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, oldPrice, discount;
        ImageView image, wishlist, ivAddIcon;
        View btnAdd;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tv_product_name);
            price = view.findViewById(R.id.tv_price);
            oldPrice = view.findViewById(R.id.tv_old_price);
            discount = view.findViewById(R.id.tv_discount);
            image = view.findViewById(R.id.iv_product);
            wishlist = view.findViewById(R.id.iv_wishlist);
            btnAdd = view.findViewById(R.id.btn_add_to_cart);
            ivAddIcon = view.findViewById(R.id.iv_add_icon);

            if (!isHorizontal) {
                ViewGroup.LayoutParams params = itemView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                itemView.setLayoutParams(params);
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel item = items.get(position);
        holder.name.setText(item.getProductName());

        String sellingPriceStr = item.getPrice() != null ? item.getPrice() : "0";
        String mrpStr = item.getMainPrice() != null ? item.getMainPrice() : sellingPriceStr;

        try {
            String sClean = sellingPriceStr.replaceAll("[^0-9.]", "");
            String mClean = mrpStr.replaceAll("[^0-9.]", "");

            if (!sClean.isEmpty() && !mClean.isEmpty()) {
                double sPrice = Double.parseDouble(sClean);
                double mPrice = Double.parseDouble(mClean);
                holder.price.setText("₹" + (int) sPrice);
                holder.oldPrice.setText("₹" + (int) mPrice);
                holder.oldPrice.setPaintFlags(holder.oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                if (mPrice > sPrice && mPrice > 0) {
                    int discountPercent = (int) Math.round(((mPrice - sPrice) / mPrice) * 100);
                    if (discountPercent > 0) {
                        holder.discount.setText(discountPercent + "% OFF");
                        holder.discount.setVisibility(View.VISIBLE);
                    } else {
                        holder.discount.setVisibility(View.GONE);
                    }
                } else {
                    holder.discount.setVisibility(View.GONE);
                }
            } else {
                holder.price.setText("₹" + sellingPriceStr);
                holder.oldPrice.setText("₹" + mrpStr);
                holder.discount.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            holder.price.setText("₹" + sellingPriceStr);
            holder.oldPrice.setText("₹" + mrpStr);
            holder.discount.setVisibility(View.GONE);
        }

        Glide.with(context).load(item.getProductImage()).placeholder(R.drawable.ic_category).into(holder.image);

        if (holder.wishlist != null) {
            if (wishlistProductIds.contains(item.getProductId())) {
                holder.wishlist.setImageResource(R.drawable.ic_heart_filled);
                holder.wishlist.setColorFilter(null);
            } else {
                holder.wishlist.setImageResource(R.drawable.ic_heart_outline);
                holder.wishlist.setColorFilter(androidx.core.content.ContextCompat.getColor(context, R.color.text_subtitle));
            }
            holder.wishlist.setOnClickListener(v -> {
                if (wishlistProductIds.contains(item.getProductId())) removeFromWishlist(item);
                else addToWishlist(item);
            });
        }

        if (holder.btnAdd != null) {
            if (cartProductIds.contains(item.getProductId())) {
                holder.ivAddIcon.setImageResource(R.drawable.ic_check);
            } else {
                holder.ivAddIcon.setImageResource(R.drawable.ic_add);
            }
            holder.btnAdd.setOnClickListener(v -> {
                if (cartProductIds.contains(item.getProductId())) removeFromCart(item);
                else addToCart(item);
            });
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product", item);
            context.startActivity(intent);
        });
    }

    private void removeFromCart(ProductModel product) {
        String userId = sessionManager.getUserId();
        if (userId.isEmpty()) return;
        String authHeader = "Bearer " + sessionManager.getToken();
        Map<String, String> filters = new HashMap<>();
        filters.put("user_id", "eq." + userId);
        filters.put("product_id", "eq." + product.getProductId());
        SupabaseClient.getApiService().deleteDataByFilters("cart", SupabaseClient.SUPABASE_ANON_KEY, authHeader, filters).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    cartProductIds.remove(product.getProductId());
                    for (int i = 0; i < CartManager.getInstance().getCartItems().size(); i++) {
                        if (CartManager.getInstance().getCartItems().get(i).getProduct().getProductId().equals(product.getProductId())) {
                            CartManager.getInstance().removeItem(i);
                            break;
                        }
                    }
                    if (callback != null) callback.onUpdate();
                    notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {}
        });
    }

    private void addToCart(ProductModel product) {
        CartHelper.addToCart(context, product, new CartHelper.CartCallback() {
            @Override
            public void onSuccess() {
                cartProductIds.add(product.getProductId());
                if (callback != null) callback.onUpdate();
                notifyDataSetChanged();
                Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(String error) {}
        });
    }

    private void addToWishlist(ProductModel product) {
        String userId = sessionManager.getUserId();
        if (userId.isEmpty()) return;
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", userId);
        data.put("product_id", product.getProductId());
        data.put("product_name", product.getProductName());
        data.put("product_price", product.getSellingPrice() != null ? product.getSellingPrice() : product.getPrice());
        data.put("product_image", product.getProductImage());
        String authHeader = "Bearer " + sessionManager.getToken();
        SupabaseClient.getApiService().addData("wishlist", SupabaseClient.SUPABASE_ANON_KEY, authHeader, "return=representation", data).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(@NonNull Call<List<Map<String, Object>>> call, @NonNull Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful()) {
                    wishlistProductIds.add(product.getProductId());
                    notifyDataSetChanged();
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
        SupabaseClient.getApiService().deleteDataByFilters("wishlist", SupabaseClient.SUPABASE_ANON_KEY, authHeader, filters).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    wishlistProductIds.remove(product.getProductId());
                    notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {}
        });
    }

    @Override
    public int getItemCount() { return items.size(); }
}

