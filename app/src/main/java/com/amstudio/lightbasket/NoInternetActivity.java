package com.amstudio.lightbasket;

import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.amstudio.lightbasket.utils.NetworkUtils;
import com.google.android.material.button.MaterialButton;

public class NoInternetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        MaterialButton btnRetry = findViewById(R.id.btnRetry);

        btnRetry.setOnClickListener(v -> {
            if (NetworkUtils.isNetworkAvailable(this)) {
                finish();
            } else {
                Toast.makeText(NoInternetActivity.this, getString(R.string.still_no_internet), Toast.LENGTH_SHORT).show();
            }
        });

        // Handle back press using modern API
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (NetworkUtils.isNetworkAvailable(NoInternetActivity.this)) {
                    setEnabled(false);
                    onBackPressed();
                } else {
                    Toast.makeText(NoInternetActivity.this, getString(R.string.connect_to_continue), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
