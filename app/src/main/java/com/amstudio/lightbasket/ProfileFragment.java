package com.amstudio.lightbasket;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.amstudio.lightbasket.api.SessionManager;
import com.bumptech.glide.Glide;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        SessionManager sessionManager = new SessionManager(requireContext());

        // Header
        TextView tvName = view.findViewById(R.id.tv_profile_name);
        TextView tvEmail = view.findViewById(R.id.tv_profile_email);
        TextView tvPhone = view.findViewById(R.id.tv_profile_phone);
        tvName.setText(sessionManager.getUserName());
        tvEmail.setText(sessionManager.getUserEmail());
        
        ImageView ivProfile = view.findViewById(R.id.iv_profile_pic);
        String profileUrl = sessionManager.getUserProfile();
        if (profileUrl != null && !profileUrl.isEmpty()) {
            Glide.with(this)
                    .load(profileUrl)
                    .placeholder(R.drawable.ic_person_24)
                    .error(R.drawable.ic_person_24)
                    .circleCrop()
                    .into(ivProfile);
        }

        String phone = sessionManager.getUserPhone();
        if (phone != null && !phone.isEmpty()) {
            tvPhone.setText("+91 " + phone);
            tvPhone.setVisibility(View.VISIBLE);
        } else {
            tvPhone.setVisibility(View.GONE);
        }

        fetchUserProfile(sessionManager, tvPhone);

        // Quick Actions
        setupQuickAction(view.findViewById(R.id.item_orders), "Orders", R.drawable.ic_box, R.color.cat_electronics_bg, R.color.accent_blue);
        setupQuickAction(view.findViewById(R.id.item_wishlist_quick), "Wishlist", R.drawable.ic_heart_outline, R.color.cat_women_bg, R.color.status_cancelled);
        setupQuickAction(view.findViewById(R.id.item_addresses), "Addresses", R.drawable.ic_location, R.color.cat_men_bg, R.color.status_delivered);
        setupQuickAction(view.findViewById(R.id.item_payments), "Edit Profile", R.drawable.ic_profile, R.color.cat_home_bg, R.color.primary);

        // My Orders Rows
        setupRow(view.findViewById(R.id.row_my_orders), "My Cart", "View and manage your cart", R.drawable.ic_cart, R.color.cat_electronics_bg, R.color.accent_blue);
        setupRow(view.findViewById(R.id.row_wishlist), "Wishlist", "Your saved products", R.drawable.ic_heart_outline, R.color.cat_women_bg, R.color.status_cancelled);

        // Support Section
        setupRow(view.findViewById(R.id.row_help_center), "Help Center", "Find answers to common questions", R.drawable.ic_help, R.color.cat_electronics_bg, R.color.accent_blue);
        setupRow(view.findViewById(R.id.row_contact_us), "Contact Us", "Get in touch with our support team", R.drawable.ic_headphones, R.color.cat_home_bg, R.color.status_processing);

        // About Section
        setupRow(view.findViewById(R.id.row_about_app), "About App", "Know more about the app", R.drawable.ic_info, R.color.divider_color, R.color.text_subtitle);
        setupRow(view.findViewById(R.id.row_terms), "Terms & Conditions", "Read our terms and conditions", R.drawable.ic_description, R.color.cat_electronics_bg, R.color.accent_blue);
        setupRow(view.findViewById(R.id.row_privacy_policy), "Privacy Policy", "Read our privacy policy", R.drawable.ic_privacy, R.color.cat_men_bg, R.color.status_delivered);

        // Click Listener
        
        View.OnClickListener ordersListener = v -> {
            if (getActivity() instanceof HomeActivity) {
                ((HomeActivity) getActivity()).switchToOrders();
            }
        };
        view.findViewById(R.id.item_orders).setOnClickListener(ordersListener);

        view.findViewById(R.id.row_my_orders).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), CartActivity.class));
        });

        view.findViewById(R.id.item_payments).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Edit Profile coming soon", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.item_addresses).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AddAddressActivity.class));
        });

        View.OnClickListener wishlistListener = v -> {
            startActivity(new Intent(getContext(), WishlistActivity.class));
        };
        view.findViewById(R.id.item_wishlist_quick).setOnClickListener(wishlistListener);
        view.findViewById(R.id.row_wishlist).setOnClickListener(wishlistListener);

        // Logout
        view.findViewById(R.id.btn_logout).setOnClickListener(v -> showLogoutDialog(sessionManager));
    }

    private void showLogoutDialog(SessionManager sessionManager) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out", (dialog, which) -> {
                    sessionManager.logout();
                    Intent intent = new Intent(getActivity(), AuthActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void fetchUserProfile(SessionManager sessionManager, TextView tvPhone) {
        String userId = sessionManager.getUserId();
        if (userId == null || userId.isEmpty()) return;

        String authHeader = "Bearer " + sessionManager.getToken();
        java.util.Map<String, String> filters = new java.util.HashMap<>();
        filters.put("uid", "eq." + userId);
        filters.put("select", "address");

        com.amstudio.lightbasket.api.SupabaseClient.getApiService().fetchDataWithFilters(
                "Users",
                com.amstudio.lightbasket.api.SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                filters
        ).enqueue(new retrofit2.Callback<java.util.List<java.util.Map<String, Object>>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<java.util.List<java.util.Map<String, Object>>> call, @NonNull retrofit2.Response<java.util.List<java.util.Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Object addressObj = response.body().get(0).get("address");
                    if (addressObj instanceof java.util.Map) {
                        java.util.Map<String, Object> address = (java.util.Map<String, Object>) addressObj;
                        if (address.containsKey("number")) {
                            String phone = (String) address.get("number");
                            if (phone != null && !phone.isEmpty()) {
                                sessionManager.setUserPhone(phone);
                                tvPhone.setText("+91 " + phone);
                                tvPhone.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<java.util.List<java.util.Map<String, Object>>> call, @NonNull Throwable t) {
                // Silent fail
            }
        });
    }

    private void setupQuickAction(View view, String label, int iconRes, int bgTintRes, int iconTintRes) {
        TextView tvLabel = view.findViewById(R.id.tv_action_label);
        ImageView ivIcon = view.findViewById(R.id.iv_action_icon);

        tvLabel.setText(label);
        ivIcon.setImageResource(iconRes);
        ivIcon.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), bgTintRes));
        ivIcon.setImageTintList(ContextCompat.getColorStateList(requireContext(), iconTintRes));
    }

    private void setupRow(View view, String title, String subtitle, int iconRes, int bgTintRes, int iconTintRes) {
        TextView tvTitle = view.findViewById(R.id.tv_row_title);
        TextView tvSubtitle = view.findViewById(R.id.tv_row_subtitle);
        ImageView ivIcon = view.findViewById(R.id.iv_row_icon);

        tvTitle.setText(title);
        if (subtitle != null) {
            tvSubtitle.setText(subtitle);
            tvSubtitle.setVisibility(View.VISIBLE);
        } else {
            tvSubtitle.setVisibility(View.GONE);
        }

        ivIcon.setImageResource(iconRes);
        ivIcon.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), bgTintRes));
        ivIcon.setImageTintList(ContextCompat.getColorStateList(requireContext(), iconTintRes));
    }
}

