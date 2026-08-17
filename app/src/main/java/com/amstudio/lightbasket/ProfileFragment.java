package com.amstudio.lightbasket;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.google.android.material.button.MaterialButton;

public class ProfileFragment extends Fragment {

    private String facebookUrl = "https://www.facebook.com";
    private String instagramUrl = "https://www.instagram.com";
    private String contactEmail = "support@litebasket.com";
    private String aboutUrl = "";
    private String termsUrl = "";
    private String privacyUrl = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshProfileData();
    }

    private void refreshProfileData() {
        if (getView() == null || getContext() == null) return;
        SessionManager sessionManager = new SessionManager(getContext());
        
        TextView tvName = getView().findViewById(R.id.tv_profile_name);
        if (tvName != null) {
            String name = sessionManager.getUserName();
            tvName.setText(name != null ? name : "User");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        try {
            Context context = getContext();
            if (context == null) return;

            SessionManager sessionManager = new SessionManager(context);

            // Header
            TextView tvName = view.findViewById(R.id.tv_profile_name);
            TextView tvEmail = view.findViewById(R.id.tv_profile_email);
            TextView tvPhone = view.findViewById(R.id.tv_profile_phone);

            if (tvName != null) {
                String name = sessionManager.getUserName();
                tvName.setText(name != null ? name : "User");
            }
            
            if (tvEmail != null) {
                String email = sessionManager.getUserEmail();
                tvEmail.setText(email != null ? email : "");
            }
            
            ImageView ivProfile = view.findViewById(R.id.iv_profile_pic);
            if (ivProfile != null) {
                String profileUrl = sessionManager.getUserProfile();
                if (profileUrl != null && !profileUrl.isEmpty()) {
                    Glide.with(this)
                            .load(profileUrl)
                            .placeholder(R.drawable.ic_person_24)
                            .error(R.drawable.ic_person_24)
                            .circleCrop()
                            .into(ivProfile);
                }
            }

            if (tvPhone != null) {
                String phone = sessionManager.getUserPhone();
                if (phone != null && !phone.isEmpty()) {
                    tvPhone.setText("+91 " + phone);
                    tvPhone.setVisibility(View.VISIBLE);
                } else {
                    tvPhone.setVisibility(View.GONE);
                }
                fetchUserProfile(sessionManager, tvPhone);
            }

            // Quick Actions
            View itemOrders = view.findViewById(R.id.item_orders);
            if (itemOrders != null) {
                setupQuickAction(itemOrders, "Orders", R.drawable.ic_box, R.color.cat_electronics_bg, R.color.accent_blue);
                itemOrders.setOnClickListener(v -> {
                    if (getActivity() instanceof HomeActivity) {
                        ((HomeActivity) getActivity()).switchToOrders();
                    }
                });
            }

            View itemWishlistQuick = view.findViewById(R.id.item_wishlist_quick);
            if (itemWishlistQuick != null) {
                setupQuickAction(itemWishlistQuick, "Wishlist", R.drawable.ic_heart_outline, R.color.cat_women_bg, R.color.status_cancelled);
                itemWishlistQuick.setOnClickListener(v -> {
                    if (getContext() != null) startActivity(new Intent(getContext(), WishlistActivity.class));
                });
            }

            View itemAddresses = view.findViewById(R.id.item_addresses);
            if (itemAddresses != null) {
                setupQuickAction(itemAddresses, "Addresses", R.drawable.ic_location, R.color.cat_men_bg, R.color.status_delivered);
                itemAddresses.setOnClickListener(v -> {
                    if (getContext() != null) startActivity(new Intent(getContext(), AddAddressActivity.class));
                });
            }

            View itemEditProfile = view.findViewById(R.id.item_edit_profile);
            if (itemEditProfile != null) {
                setupQuickAction(itemEditProfile, "Edit Profile", R.drawable.baseline_edit_24, R.color.cat_home_bg, R.color.primary);
                itemEditProfile.setOnClickListener(v -> {
                    if (getContext() != null) startActivity(new Intent(getContext(), EditProfileActivity.class));
                });
            }

            // My Orders Rows
            View rowMyOrders = view.findViewById(R.id.row_my_orders);
            if (rowMyOrders != null) {
                setupRow(rowMyOrders, "My Cart", "View and manage your cart", R.drawable.ic_cart, R.color.cat_electronics_bg, R.color.accent_blue);
                rowMyOrders.setOnClickListener(v -> {
                    if (getContext() != null) startActivity(new Intent(getContext(), CartActivity.class));
                });
            }

            View rowWishlist = view.findViewById(R.id.row_wishlist);
            if (rowWishlist != null) {
                setupRow(rowWishlist, "Wishlist", "Your saved products", R.drawable.ic_heart_outline, R.color.cat_women_bg, R.color.status_cancelled);
                rowWishlist.setOnClickListener(v -> {
                    if (getContext() != null) startActivity(new Intent(getContext(), WishlistActivity.class));
                });
            }

            // Support Section
            View rowContactUs = view.findViewById(R.id.row_contact_us);
            if (rowContactUs != null) {
                setupRow(rowContactUs, "Contact Us", "Get in touch with our support team", R.drawable.ic_headphones, R.color.cat_home_bg, R.color.status_processing);
                rowContactUs.setOnClickListener(v -> showContactDialog());
            }

            // Social Section
            View rowFacebook = view.findViewById(R.id.row_facebook);
            if (rowFacebook != null) {
                setupRow(rowFacebook, "Facebook", "Follow us on Facebook", R.drawable.ic_facebook, R.color.cat_electronics_bg, R.color.accent_blue);
                rowFacebook.setOnClickListener(v -> {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(facebookUrl)));
                    } catch (Exception e) { e.printStackTrace(); }
                });
            }

            View rowInstagram = view.findViewById(R.id.row_instagram);
            if (rowInstagram != null) {
                setupRow(rowInstagram, "Instagram", "Follow us on Instagram", R.drawable.ic_instagram, R.color.cat_women_bg, R.color.status_cancelled);
                rowInstagram.setOnClickListener(v -> {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(instagramUrl)));
                    } catch (Exception e) { e.printStackTrace(); }
                });
            }

            // About Section
            View rowAboutApp = view.findViewById(R.id.row_about_app);
            if (rowAboutApp != null) {
                setupRow(rowAboutApp, "About App", "Know more about the app", R.drawable.ic_info, R.color.divider_color, R.color.text_subtitle);
                rowAboutApp.setOnClickListener(v -> {
                    if (aboutUrl != null && !aboutUrl.isEmpty()) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(aboutUrl)));
                        } catch (Exception e) { e.printStackTrace(); }
                    } else {
                        Toast.makeText(getContext(), "URL not available", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            View rowTerms = view.findViewById(R.id.row_terms);
            if (rowTerms != null) {
                setupRow(rowTerms, "Terms & Conditions", "Read our terms and conditions", R.drawable.ic_description, R.color.cat_electronics_bg, R.color.accent_blue);
                rowTerms.setOnClickListener(v -> {
                    if (termsUrl != null && !termsUrl.isEmpty()) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(termsUrl)));
                        } catch (Exception e) { e.printStackTrace(); }
                    } else {
                        Toast.makeText(getContext(), "URL not available", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            View rowPrivacy = view.findViewById(R.id.row_privacy_policy);
            if (rowPrivacy != null) {
                setupRow(rowPrivacy, "Privacy Policy", "Read our privacy policy", R.drawable.ic_privacy, R.color.cat_men_bg, R.color.status_delivered);
                rowPrivacy.setOnClickListener(v -> {
                    if (privacyUrl != null && !privacyUrl.isEmpty()) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(privacyUrl)));
                        } catch (Exception e) { e.printStackTrace(); }
                    } else {
                        Toast.makeText(getContext(), "URL not available", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            fetchAppConfig();

            // Logout
            View btnLogout = view.findViewById(R.id.btn_logout);
            if (btnLogout != null) {
                btnLogout.setOnClickListener(v -> showLogoutDialog(sessionManager));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showContactDialog() {
        if (!isAdded() || getContext() == null) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_contact_us, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextView tvEmail = dialogView.findViewById(R.id.tv_email_address);
        ImageView btnCopyEmail = dialogView.findViewById(R.id.btn_copy_email);
        MaterialButton btnEmail = dialogView.findViewById(R.id.btn_email_now);
        MaterialButton btnClose = dialogView.findViewById(R.id.btn_close);

        if (tvEmail != null) tvEmail.setText(contactEmail);

        if (btnCopyEmail != null) {
            btnCopyEmail.setOnClickListener(v -> {
                if (getContext() == null) return;
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Email Address", contactEmail);
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getContext(), "Email address copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnEmail != null) {
            btnEmail.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{contactEmail});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Support Request - Light Shop App");
                    startActivity(Intent.createChooser(intent, "Send Email"));
                } catch (Exception e) {
                    e.printStackTrace();
                    if (isAdded() && getContext() != null) {
                        Toast.makeText(getContext(), "No email app found", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void fetchAppConfig() {
        try {
            com.amstudio.lightbasket.api.SupabaseClient.getApiService().fetchData(
                    "app_config",
                    com.amstudio.lightbasket.api.SupabaseClient.SUPABASE_ANON_KEY,
                    "Bearer " + com.amstudio.lightbasket.api.SupabaseClient.SUPABASE_ANON_KEY,
                    "*"
            ).enqueue(new retrofit2.Callback<java.util.List<java.util.Map<String, Object>>>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<java.util.List<java.util.Map<String, Object>>> call, @NonNull retrofit2.Response<java.util.List<java.util.Map<String, Object>>> response) {
                    if (!isAdded()) return;
                    try {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            java.util.Map<String, Object> config = response.body().get(0);
                            if (config.containsKey("facebook_url")) facebookUrl = String.valueOf(config.get("facebook_url"));
                            if (config.containsKey("instagram_url")) instagramUrl = String.valueOf(config.get("instagram_url"));
                            if (config.containsKey("contact_email")) contactEmail = String.valueOf(config.get("contact_email"));
                            if (config.containsKey("about_url")) aboutUrl = String.valueOf(config.get("about_url"));
                            if (config.containsKey("terms_conditions_url")) termsUrl = String.valueOf(config.get("terms_conditions_url"));
                            if (config.containsKey("privacy_policy_url")) privacyUrl = String.valueOf(config.get("privacy_policy_url"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<java.util.List<java.util.Map<String, Object>>> call, @NonNull Throwable t) {
                    if (!isAdded()) return;
                    // Keep default values
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showLogoutDialog(SessionManager sessionManager) {
        if (getContext() == null) return;
        new AlertDialog.Builder(getContext())
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out", (dialog, which) -> {
                    sessionManager.logout();
                    if (getActivity() != null) {
                        Intent intent = new Intent(getActivity(), AuthActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        getActivity().finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void fetchUserProfile(SessionManager sessionManager, TextView tvPhone) {
        try {
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
                    if (!isAdded()) return;
                    try {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            Object addressObj = response.body().get(0).get("address");
                            if (addressObj instanceof java.util.Map) {
                                java.util.Map<String, Object> address = (java.util.Map<String, Object>) addressObj;
                                if (address.containsKey("number")) {
                                    String phone = String.valueOf(address.get("number"));
                                    if (phone != null && !phone.isEmpty() && !phone.equals("null")) {
                                        sessionManager.setUserPhone(phone);
                                        tvPhone.setText("+91 " + phone);
                                        tvPhone.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<java.util.List<java.util.Map<String, Object>>> call, @NonNull Throwable t) {
                    if (!isAdded()) return;
                    // Silent fail
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupQuickAction(View view, String label, int iconRes, int bgTintRes, int iconTintRes) {
        if (view == null || getContext() == null) return;
        TextView tvLabel = view.findViewById(R.id.tv_action_label);
        ImageView ivIcon = view.findViewById(R.id.iv_action_icon);

        tvLabel.setText(label);
        ivIcon.setImageResource(iconRes);
        ivIcon.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), bgTintRes));
        ivIcon.setImageTintList(ContextCompat.getColorStateList(getContext(), iconTintRes));
    }

    private void setupRow(View view, String title, String subtitle, int iconRes, int bgTintRes, int iconTintRes) {
        if (view == null || getContext() == null) return;
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
        ivIcon.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), bgTintRes));
        ivIcon.setImageTintList(ContextCompat.getColorStateList(getContext(), iconTintRes));
    }
}
