package com.example.lightshop;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lightshop.api.SupabaseClient;
import com.example.lightshop.models.AuthModels;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends AppCompatActivity {

    private TextView tvTitle, tvSubtitle;
    private LinearLayout loginContainer, registerContainer;
    private ViewGroup rootLayout;
    private TextView tvFooterLogin, tvFooterRegister;
    private CheckBox cbTerms;
    private View btnLogin, btnRegister;

    // Input fields
    private EditText etLoginEmail, etLoginPassword;
    private EditText etRegName, etRegEmail, etRegPassword, etRegConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);
        
        rootLayout = findViewById(android.R.id.content);
        View root = rootLayout.getChildAt(0);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupSpannables();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        tvSubtitle = findViewById(R.id.tv_subtitle);
        loginContainer = findViewById(R.id.loginContainer);
        registerContainer = findViewById(R.id.registerContainer);
        tvFooterLogin = findViewById(R.id.tv_footer_login);
        tvFooterRegister = findViewById(R.id.tv_footer_register);
        cbTerms = findViewById(R.id.cb_terms);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);

        // Initialize Input fields
        etLoginEmail = findViewById(R.id.et_login_email);
        etLoginPassword = findViewById(R.id.et_login_password);
        etRegName = findViewById(R.id.et_reg_name);
        etRegEmail = findViewById(R.id.et_reg_email);
        etRegPassword = findViewById(R.id.et_reg_password);
        etRegConfirm = findViewById(R.id.et_reg_confirm);

        btnLogin.setOnClickListener(v -> handleLogin());

        btnRegister.setOnClickListener(v -> handleRegister());
    }

    private void handleLogin() {
        String email = etLoginEmail.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);
        String authHeader = "Bearer " + SupabaseClient.SUPABASE_ANON_KEY;
        AuthModels.LoginRequest request = new AuthModels.LoginRequest(email, password);
        SupabaseClient.getAuthService().login(SupabaseClient.SUPABASE_ANON_KEY, authHeader, request)
                .enqueue(new Callback<AuthModels.AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthModels.AuthResponse> call, Response<AuthModels.AuthResponse> response) {
                        btnLogin.setEnabled(true);
                        if (response.isSuccessful() && response.body() != null) {
                            startActivity(new Intent(AuthActivity.this, HomeActivity.class));
                            finish();
                        } else {
                            String errorMsg = parseError(response);
                            Toast.makeText(AuthActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthModels.AuthResponse> call, Throwable t) {
                        btnLogin.setEnabled(true);
                        Toast.makeText(AuthActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleRegister() {
        String name = etRegName.getText().toString().trim();
        String email = etRegEmail.getText().toString().trim();
        String password = etRegPassword.getText().toString().trim();
        String confirm = etRegConfirm.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirm)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Please agree to the terms", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegister.setEnabled(false);
        String authHeader = "Bearer " + SupabaseClient.SUPABASE_ANON_KEY;
        AuthModels.SignUpRequest request = new AuthModels.SignUpRequest(email, password, name);
        SupabaseClient.getAuthService().signUp(SupabaseClient.SUPABASE_ANON_KEY, authHeader, request)
                .enqueue(new Callback<AuthModels.AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthModels.AuthResponse> call, Response<AuthModels.AuthResponse> response) {
                        btnRegister.setEnabled(true);
                        if (response.isSuccessful()) {
                            Toast.makeText(AuthActivity.this, "Registration successful!", Toast.LENGTH_LONG).show();
                            showLogin();
                        } else {
                            String errorMsg = parseError(response);
                            Toast.makeText(AuthActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthModels.AuthResponse> call, Throwable t) {
                        btnRegister.setEnabled(true);
                        Toast.makeText(AuthActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupSpannables() {
        // Footer Login (Don't have an account? Register)
        String footerLoginText = "Don't have an account? Register";
        SpannableString ssLogin = new SpannableString(footerLoginText);
        ClickableSpan csRegister = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                showRegister();
            }
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setFakeBoldText(true);
            }
        };
        ssLogin.setSpan(csRegister, 23, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssLogin.setSpan(new ForegroundColorSpan(getColor(R.color.primary)), 23, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvFooterLogin.setText(ssLogin);
        tvFooterLogin.setMovementMethod(LinkMovementMethod.getInstance());
        tvFooterLogin.setHighlightColor(Color.TRANSPARENT);

        // Footer Register (Already have an account? Login)
        String footerRegisterText = "Already have an account? Login";
        SpannableString ssRegister = new SpannableString(footerRegisterText);
        ClickableSpan csLogin = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                showLogin();
            }
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setFakeBoldText(true);
            }
        };
        ssRegister.setSpan(csLogin, 25, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssRegister.setSpan(new ForegroundColorSpan(getColor(R.color.primary)), 25, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvFooterRegister.setText(ssRegister);
        tvFooterRegister.setMovementMethod(LinkMovementMethod.getInstance());
        tvFooterRegister.setHighlightColor(Color.TRANSPARENT);

        // Terms & Conditions
        String termsText = "I agree to the Terms & Conditions and Privacy Policy";
        SpannableString ssTerms = new SpannableString(termsText);
        ssTerms.setSpan(new ForegroundColorSpan(getColor(R.color.primary)), 15, 33, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssTerms.setSpan(new ForegroundColorSpan(getColor(R.color.primary)), 38, ssTerms.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        cbTerms.setText(ssTerms);
    }

    private void showLogin() {
        if (loginContainer.getVisibility() == View.VISIBLE) return;

        applyTransition(Gravity.START);

        tvTitle.setText("Login");
        tvSubtitle.setText("Welcome back! Please login\nto continue");

        loginContainer.setVisibility(View.VISIBLE);
        registerContainer.setVisibility(View.GONE);
    }

    private void showRegister() {
        if (registerContainer.getVisibility() == View.VISIBLE) return;

        applyTransition(Gravity.END);

        tvTitle.setText("Register");
        tvSubtitle.setText("Create your account to\nget started");

        loginContainer.setVisibility(View.GONE);
        registerContainer.setVisibility(View.VISIBLE);
    }

    private void applyTransition(int gravity) {
        TransitionSet transition = new TransitionSet();
        transition.addTransition(new Fade());
        transition.addTransition(new Slide(gravity));
        transition.setDuration(250);
        TransitionManager.beginDelayedTransition((ViewGroup) rootLayout.getChildAt(0), transition);
    }

    private String parseError(Response<AuthModels.AuthResponse> response) {
        try {
            if (response.errorBody() != null) {
                String errorJson = response.errorBody().string();
                android.util.Log.e("SupabaseError", "Error Body: " + errorJson);
                AuthModels.AuthResponse errorResponse = new com.google.gson.Gson().fromJson(errorJson, AuthModels.AuthResponse.class);
                if (errorResponse != null) {
                    if (errorResponse.errorDescription != null && !errorResponse.errorDescription.isEmpty()) 
                        return errorResponse.errorDescription;
                    if (errorResponse.error != null && !errorResponse.error.isEmpty()) 
                        return errorResponse.error;
                }
                return "Error " + response.code() + ": " + errorJson;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Error " + response.code() + ". Please try again.";
    }
}
