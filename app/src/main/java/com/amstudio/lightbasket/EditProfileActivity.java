package com.amstudio.lightbasket;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.amstudio.lightbasket.api.SessionManager;
import com.amstudio.lightbasket.api.SupabaseClient;
import com.amstudio.lightbasket.utils.StatusBarUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.HashMap;
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText etName;
    private MaterialButton btnSave;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.applyWhiteStatusBar(this);
        setContentView(R.layout.activity_edit_profile);

        sessionManager = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        etName = findViewById(R.id.et_name);
        btnSave = findViewById(R.id.btn_save);

        // Pre-fill current name
        String currentName = sessionManager.getUserName();
        if (currentName != null && !currentName.isEmpty()) {
            etName.setText(currentName);
        }

        btnSave.setOnClickListener(v -> updateName());
    }

    private void updateName() {
        String newName = etName.getText() != null ? etName.getText().toString().trim() : "";

        if (TextUtils.isEmpty(newName)) {
            etName.setError("Name is required");
            return;
        }

        String userId = sessionManager.getUserId();
        if (TextUtils.isEmpty(userId)) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSave.setEnabled(false);
        btnSave.setText("Updating...");

        String authHeader = "Bearer " + sessionManager.getToken();
        
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("name", newName);

        Map<String, String> filters = new HashMap<>();
        filters.put("uid", "eq." + userId);

        SupabaseClient.getApiService().updateDataByColumn(
                "Users",
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                filters,
                updateData
        ).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                btnSave.setEnabled(true);
                btnSave.setText("Save Changes");

                if (response.isSuccessful()) {
                    sessionManager.setUserName(newName); // Update local session
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Failed to update profile. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                btnSave.setEnabled(true);
                btnSave.setText("Save Changes");
                Toast.makeText(EditProfileActivity.this, "Network error. Please check your connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
