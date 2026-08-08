package com.example.lightshop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.lightshop.api.SessionManager;

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
        tvName.setText(sessionManager.getUserName());
        tvEmail.setText(sessionManager.getUserEmail());

        // Quick Actions
        setupQuickAction(view.findViewById(R.id.item_orders), "Orders", R.drawable.ic_box, R.color.cat_electronics_bg, R.color.accent_blue);
        setupQuickAction(view.findViewById(R.id.item_wishlist_quick), "Wishlist", R.drawable.ic_heart_outline, R.color.cat_women_bg, R.color.status_cancelled);
        setupQuickAction(view.findViewById(R.id.item_addresses), "Addresses", R.drawable.ic_location, R.color.cat_men_bg, R.color.status_delivered);
        setupQuickAction(view.findViewById(R.id.item_payments), "Payments", R.drawable.ic_credit_card, R.color.cat_home_bg, R.color.status_processing);

        // My Orders Rows
        setupRow(view.findViewById(R.id.row_my_orders), "My Orders", "View and manage your orders", R.drawable.ic_box, R.color.cat_electronics_bg, R.color.accent_blue);
        setupRow(view.findViewById(R.id.row_track_orders), "Track Orders", "Track your current deliveries", R.drawable.ic_car, R.color.cat_beauty_bg, R.color.secondary);
        setupRow(view.findViewById(R.id.row_wishlist), "Wishlist", "Your saved products", R.drawable.ic_heart_outline, R.color.cat_women_bg, R.color.status_cancelled);

        // Click Listeners
        view.findViewById(R.id.btn_edit_profile).setOnClickListener(v -> Toast.makeText(getContext(), "Edit Profile clicked", Toast.LENGTH_SHORT).show());
        
        view.findViewById(R.id.iv_settings).setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SettingsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        view.findViewById(R.id.iv_notifications).setOnClickListener(v -> Toast.makeText(getContext(), "Notifications clicked", Toast.LENGTH_SHORT).show());
        
        View.OnClickListener ordersListener = v -> {
            if (getActivity() instanceof HomeActivity) {
                ((HomeActivity) getActivity()).switchToOrders();
            }
        };
        view.findViewById(R.id.item_orders).setOnClickListener(ordersListener);
        view.findViewById(R.id.row_my_orders).setOnClickListener(ordersListener);
    }

    private void setupQuickAction(View view, String label, int iconRes, int bgTintRes, int iconTintRes) {
        TextView tvLabel = view.findViewById(R.id.tv_action_label);
        ImageView ivIcon = view.findViewById(R.id.iv_action_icon);

        tvLabel.setText(label);
        ivIcon.setImageResource(iconRes);
        ivIcon.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), bgTintRes));
        ivIcon.setImageTintList(ContextCompat.getColorStateList(requireContext(), iconTintRes));
        
        view.setOnClickListener(v -> Toast.makeText(getContext(), label + " clicked", Toast.LENGTH_SHORT).show());
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

        view.setOnClickListener(v -> Toast.makeText(getContext(), title + " clicked", Toast.LENGTH_SHORT).show());
    }
}
