package com.amstudio.lightbasket.utils;

import android.content.Context;
import com.amstudio.lightbasket.api.SessionManager;
import com.amstudio.lightbasket.api.SupabaseClient;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserUtils {
    public interface AddressCheckCallback {
        void onResult(boolean hasAddress);
    }

    public static void checkUserAddress(Context context, AddressCheckCallback callback) {
        SessionManager sessionManager = new SessionManager(context);
        String userId = sessionManager.getUserId();
        if (userId.isEmpty()) {
            callback.onResult(false);
            return;
        }

        String authHeader = "Bearer " + sessionManager.getToken();
        Map<String, String> filters = new java.util.HashMap<>();
        filters.put("uid", "eq." + userId);
        filters.put("select", "address");

        SupabaseClient.getApiService().fetchDataWithFilters(
                "Users",
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                filters
        ).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Object addressObj = response.body().get(0).get("address");
                    if (addressObj instanceof Map && !((Map) addressObj).isEmpty()) {
                        callback.onResult(true);
                        return;
                    }
                }
                callback.onResult(false);
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                callback.onResult(false);
            }
        });
    }
}

