package com.example.lightshop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.lightshop.databinding.ActivityProfileBinding;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupMyAccountRecyclerView();
        setupMoreRecyclerView();
        setupBottomNavigation();
    }

    private void setupMyAccountRecyclerView() {
        List<ProfileMenuModel> myAccountItems = new ArrayList<>();
        myAccountItems.add(new ProfileMenuModel(1, "Personal Information", R.drawable.ic_person_24));
        myAccountItems.add(new ProfileMenuModel(2, "Address Book", R.drawable.ic_location));
        myAccountItems.add(new ProfileMenuModel(3, "Payment Methods", R.drawable.ic_credit_card));
        myAccountItems.add(new ProfileMenuModel(4, "Notifications", R.drawable.ic_notification));
        myAccountItems.add(new ProfileMenuModel(5, "Help & Support", R.drawable.ic_help));

        binding.rvMyAccount.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMyAccount.setAdapter(new ProfileAdapter(myAccountItems, item -> {
            Toast.makeText(ProfileActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
        }));
    }

    private void setupMoreRecyclerView() {
        List<ProfileMenuModel> moreItems = new ArrayList<>();
        moreItems.add(new ProfileMenuModel(6, "About Us", R.drawable.ic_info));
        moreItems.add(new ProfileMenuModel(7, "Privacy Policy", R.drawable.ic_privacy));
        moreItems.add(new ProfileMenuModel(8, "Terms & Conditions", R.drawable.ic_description));
        moreItems.add(new ProfileMenuModel(9, "Logout", R.drawable.ic_logout, R.color.color_logout, R.color.color_logout));

        binding.rvMore.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMore.setAdapter(new ProfileAdapter(moreItems, item -> {
            Toast.makeText(ProfileActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
        }));
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setSelectedItemId(R.id.nav_profile);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_category) {
                startActivity(new Intent(this, CategoryActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_orders) {
                startActivity(new Intent(this, MyOrdersActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }
}
