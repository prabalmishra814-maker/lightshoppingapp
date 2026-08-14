package com.amstudio.lightbasket;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.amstudio.lightbasket.api.SupabaseClient;
import com.amstudio.lightbasket.utils.NetworkUtils;

public class LightShopApplication extends Application {
    
    private Activity currentActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        SupabaseClient.init(this);
        
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {}

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                currentActivity = activity;
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                currentActivity = activity;
                // Check on resume to handle cases where network was lost while app was in background
                checkNetworkAndRedirect(activity);
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                if (currentActivity == activity) {
                    currentActivity = null;
                }
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {}

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {}
        });

        registerNetworkCallback();
    }

    private void registerNetworkCallback() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            connectivityManager.registerNetworkCallback(new NetworkRequest.Builder().build(), 
                new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onLost(@NonNull Network network) {
                        super.onLost(network);
                        // Network lost, check if we have ANY network left
                        if (!NetworkUtils.isNetworkAvailable(LightShopApplication.this)) {
                            redirectToNoInternet();
                        }
                    }

                    @Override
                    public void onAvailable(@NonNull Network network) {
                        super.onAvailable(network);
                        // Network available, if we were on NoInternetActivity, it will handle it via retry
                    }
                });
        }
    }

    private void checkNetworkAndRedirect(Activity activity) {
        if (!(activity instanceof NoInternetActivity) && !(activity instanceof SplashActivity)) {
            if (!NetworkUtils.isNetworkAvailable(this)) {
                redirectToNoInternet();
            }
        }
    }

    private void redirectToNoInternet() {
        if (currentActivity != null && !(currentActivity instanceof NoInternetActivity) && !(currentActivity instanceof SplashActivity)) {
            Intent intent = new Intent(currentActivity, NoInternetActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}

