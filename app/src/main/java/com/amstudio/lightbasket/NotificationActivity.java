package com.amstudio.lightbasket;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.amstudio.lightbasket.api.SessionManager;
import com.amstudio.lightbasket.api.SupabaseClient;
import com.amstudio.lightbasket.models.NotificationModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity implements NotificationAdapter.OnNotificationListener {

    private RecyclerView rvNotifications;
    private NotificationAdapter adapter;
    private List<NotificationModel> notificationList = new ArrayList<>();
    private ProgressBar progressBar;
    private View emptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        setupToolbar();
        initViews();
        fetchNotifications();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initViews() {
        rvNotifications = findViewById(R.id.rv_notifications);
        progressBar = findViewById(R.id.progress_bar);
        emptyState = findViewById(R.id.empty_state);

        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(notificationList, this);
        rvNotifications.setAdapter(adapter);
    }

    @Override
    public void onDelete(int position) {
        if (position < 0 || position >= notificationList.size()) return;
        
        NotificationModel notification = notificationList.get(position);
        SessionManager sessionManager = new SessionManager(this);
        String authHeader = "Bearer " + sessionManager.getToken();
        
        Map<String, String> filters = new HashMap<>();
        filters.put("id", "eq." + notification.getId());

        SupabaseClient.getApiService().deleteDataByFilters(
                "notifications",
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                filters
        ).enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<okhttp3.ResponseBody> call, @NonNull Response<okhttp3.ResponseBody> response) {
                if (response.isSuccessful()) {
                    notificationList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, notificationList.size());
                    
                    if (notificationList.isEmpty()) {
                        emptyState.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(NotificationActivity.this, "Failed to delete notification", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<okhttp3.ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(NotificationActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchNotifications() {
        SessionManager sessionManager = new SessionManager(this);
        String userId = sessionManager.getUserId();

        if (userId.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        
        Map<String, String> filters = new HashMap<>();
        // Fetch both user-specific notifications and general announcements (uid is null)
        filters.put("or", "(uid.eq." + userId + ",uid.is.null)");
        filters.put("order", "created_at.desc");

        String authHeader = "Bearer " + sessionManager.getToken();
        
        android.util.Log.d("Notifications", "Fetching for user: " + userId);
        SupabaseClient.getApiService().fetchNotifications(
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "*",
                filters
        ).enqueue(new Callback<List<NotificationModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<NotificationModel>> call, @NonNull Response<List<NotificationModel>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    notificationList.clear();
                    notificationList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    android.util.Log.d("Notifications", "Count: " + notificationList.size());
                    
                    if (notificationList.isEmpty()) {
                        emptyState.setVisibility(View.VISIBLE);
                    } else {
                        emptyState.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(NotificationActivity.this, "Something went wrong. Please contact the developer.", Toast.LENGTH_SHORT).show();
                    emptyState.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<NotificationModel>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(NotificationActivity.this, "Something went wrong. Please contact the developer.", Toast.LENGTH_SHORT).show();
                emptyState.setVisibility(View.VISIBLE);
            }
        });
    }
}

