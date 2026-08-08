package com.example.lightshop;

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
import com.example.lightshop.api.SessionManager;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        SessionManager sessionManager = new SessionManager(requireContext());

        // Back button
        view.findViewById(R.id.iv_back).setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // Account Section
        setupRow(view.findViewById(R.id.row_personal_info), "Personal Information", "Manage your name, email and phone number", R.drawable.ic_person_24, R.color.cat_electronics_bg, R.color.accent_blue);
        setupRow(view.findViewById(R.id.row_saved_addresses), "Saved Addresses", "Manage your delivery addresses", R.drawable.ic_location, R.color.cat_men_bg, R.color.status_delivered);
        setupRow(view.findViewById(R.id.row_payment_methods), "Payment Methods", "Manage saved payment methods", R.drawable.ic_credit_card, R.color.cat_home_bg, R.color.status_processing);

        // Preferences Section
        setupRow(view.findViewById(R.id.row_notifications), "Notifications", "Manage notification preferences", R.drawable.baseline_notifications_24, R.color.cat_beauty_bg, R.color.secondary);
        setupRow(view.findViewById(R.id.row_privacy), "Privacy & Security", "Password and security settings", R.drawable.ic_privacy, R.color.cat_electronics_bg, R.color.accent_blue);
        
        View languageRow = view.findViewById(R.id.row_language);
        setupRow(languageRow, "Language", "Change app language", R.drawable.ic_all_categories, R.color.cat_electronics_bg, R.color.accent_blue);
        // Add "English" text on the right for language row (special case if needed, but let's keep it simple for now)

        // Support Section
        setupRow(view.findViewById(R.id.row_help_center), "Help Center", "Find answers to common questions", R.drawable.ic_help, R.color.cat_electronics_bg, R.color.accent_blue);
        setupRow(view.findViewById(R.id.row_contact_us), "Contact Us", "Get in touch with our support team", R.drawable.ic_headphones, R.color.cat_home_bg, R.color.status_processing);
        setupRow(view.findViewById(R.id.row_report_problem), "Report a Problem", "Report issues or bugs in the app", R.drawable.ic_info, R.color.cat_women_bg, R.color.status_cancelled);

        // About Section
        setupRow(view.findViewById(R.id.row_about_app), "About App", "Know more about the app", R.drawable.ic_info, R.color.divider_color, R.color.text_subtitle);
        setupRow(view.findViewById(R.id.row_terms), "Terms & Conditions", "Read our terms and conditions", R.drawable.ic_description, R.color.cat_electronics_bg, R.color.accent_blue);
        setupRow(view.findViewById(R.id.row_privacy_policy), "Privacy Policy", "Read our privacy policy", R.drawable.ic_privacy, R.color.cat_men_bg, R.color.status_delivered);

        // Logout
        view.findViewById(R.id.btn_logout).setOnClickListener(v -> showLogoutDialog(sessionManager));
    }

    private void setupRow(View view, String title, String subtitle, int iconRes, int bgTintRes, int iconTintRes) {
        TextView tvTitle = view.findViewById(R.id.tv_row_title);
        TextView tvSubtitle = view.findViewById(R.id.tv_row_subtitle);
        ImageView ivIcon = view.findViewById(R.id.iv_row_icon);

        tvTitle.setText(title);
        if (subtitle != null) {
            tvSubtitle.setText(subtitle);
            tvSubtitle.setVisibility(View.VISIBLE);
        }

        ivIcon.setImageResource(iconRes);
        ivIcon.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), bgTintRes));
        ivIcon.setImageTintList(ContextCompat.getColorStateList(requireContext(), iconTintRes));

        view.setOnClickListener(v -> Toast.makeText(getContext(), title + " clicked", Toast.LENGTH_SHORT).show());
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
}
