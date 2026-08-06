package com.example.lightshop.utils;

import android.graphics.Color;
import android.view.View;
import android.view.Window;
import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

/**
 * Utility class to manage System Bar (Status & Navigation) appearance consistently across the app.
 */
public class StatusBarUtils {

    /**
     * Configures the activity to have a white status bar and navigation bar with dark icons.
     * This handles Edge-to-Edge display and ensures content doesn't overlap with system bars.
     *
     * @param activity The activity to apply the system bar styles to.
     */
    public static void applyWhiteStatusBar(ComponentActivity activity) {
        // 1. Enable Edge-to-Edge with Light style for both status and navigation bars
        // We use Color.WHITE for the scrims to ensure they are never black
        EdgeToEdge.enable(activity, 
            SystemBarStyle.light(Color.WHITE, Color.WHITE),
            SystemBarStyle.light(Color.WHITE, Color.WHITE)
        );

        // 2. Explicitly set light appearance to ensure dark icons
        Window window = activity.getWindow();
        if (window != null) {
            // Fallback for older APIs
            window.setStatusBarColor(Color.WHITE);
            window.setNavigationBarColor(Color.WHITE);
            
            WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(window, window.getDecorView());
            // Dark icons for status bar
            controller.setAppearanceLightStatusBars(true);
            // Dark icons for navigation bar
            controller.setAppearanceLightNavigationBars(true);
        }

        // 3. Apply padding to the root view so content stays within the visible area
        View rootView = activity.findViewById(android.R.id.content);
        if (rootView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return WindowInsetsCompat.CONSUMED;
            });
        }
    }
}
