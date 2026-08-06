package com.example.lightshop;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationHelper {

    public static void setupBottomNavigation(final Activity activity, int currentItemId) {
        BottomNavigationView bottomNav = activity.findViewById(R.id.bottom_nav);
        if (bottomNav == null) return;

        // Set selected item correctly
        bottomNav.setSelectedItemId(currentItemId);

        // Handle navigation
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == currentItemId) return true;

            Class<?> targetActivity = null;
            if (itemId == R.id.nav_home) {
                targetActivity = HomeActivity.class;
            } else if (itemId == R.id.nav_category) {
                targetActivity = CategoryActivity.class;
            } else if (itemId == R.id.nav_orders) {
                targetActivity = MyOrdersActivity.class;
            } else if (itemId == R.id.nav_profile) {
                targetActivity = ProfileActivity.class;
            }

            if (targetActivity != null) {
                Intent intent = new Intent(activity, targetActivity);
                // reorder to front to avoid duplicate activities and maintain state
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activity.startActivity(intent);
                // Use a smooth transition
                activity.overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }
}
