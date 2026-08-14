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

public class NotificationActivity extends AppCompatActivity {

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
        adapter = new NotificationAdapter(notificationList);
        rvNotifications.setAdapter(adapter);
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
        filters.put("uid", "eq." + userId);
        filters.put("order", "created_at.desc");

        String authHeader = "Bearer " + sessionManager.getToken();
        
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
                    
                    if (notificationList.isEmpty()) {
                        emptyState.setVisibility(View.VISIBLE);
                    } else {
                        emptyState.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(NotificationActivity.this, "Failed to load notifications", Toast.LENGTH_SHORT).show();
                    emptyState.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<NotificationModel>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(NotificationActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                emptyState.setVisibility(View.VISIBLE);
            }
        });
    }
}

