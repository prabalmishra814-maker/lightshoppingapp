package com.amstudio.lightbasket;

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
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.amstudio.lightbasket.api.SessionManager;
import com.amstudio.lightbasket.api.SupabaseClient;
import com.amstudio.lightbasket.models.AuthModels;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends AppCompatActivity {

    private TextView tvTitle, tvSubtitle;
    private LinearLayout loginContainer, registerContainer;
    private TextView tvFooterLogin, tvFooterRegister;
    private CheckBox cbTerms;
    private View btnLogin, btnRegister, btnGoogle;

    private static final String GOOGLE_WEB_CLIENT_ID = "611486037764-vb5ado4a5tkrpe339vvaehkre0nskn6v.apps.googleusercontent.com";

    // Input fields
    private EditText etLoginEmail, etLoginPassword;
    private EditText etRegName, etRegEmail, etRegPassword, etRegConfirm;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_auth);

        View root = findViewById(R.id.auth_root);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupSpannables();
        setupBackNavigation();
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
        btnGoogle = findViewById(R.id.btn_google);

        // Initialize Input fields
        etLoginEmail = findViewById(R.id.et_login_email);
        etLoginPassword = findViewById(R.id.et_login_password);
        etRegName = findViewById(R.id.et_reg_name);
        etRegEmail = findViewById(R.id.et_reg_email);
        etRegPassword = findViewById(R.id.et_reg_password);
        etRegConfirm = findViewById(R.id.et_reg_confirm);

        btnLogin.setOnClickListener(v -> handleLogin());
        btnRegister.setOnClickListener(v -> handleRegister());
        btnGoogle.setOnClickListener(v -> handleGoogleLogin());

    }

    private void setupBackNavigation() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (registerContainer.getVisibility() == View.VISIBLE) {
                    showLogin();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void handleGoogleLogin() {
        CredentialManager credentialManager = CredentialManager.create(this);

        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(GOOGLE_WEB_CLIENT_ID)
                .setAutoSelectEnabled(true)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager.getCredentialAsync(this, request, null, ContextCompat.getMainExecutor(this),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        if (result.getCredential() instanceof CustomCredential) {
                            CustomCredential custom = (CustomCredential) result.getCredential();
                            if (custom.getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
                                try {
                                    GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(custom.getData());
                                    String idToken = googleIdTokenCredential.getIdToken();
                                    signInWithSupabaseGoogle(idToken);
                                } catch (Exception e) {
                                    Toast.makeText(AuthActivity.this, "Google login failed. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        Toast.makeText(AuthActivity.this, "Google login failed. Please check your connection.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signInWithSupabaseGoogle(String idToken) {
        btnGoogle.setEnabled(false);
        String authHeader = "Bearer " + SupabaseClient.SUPABASE_ANON_KEY;
        AuthModels.IdTokenRequest request = new AuthModels.IdTokenRequest(idToken);

        SupabaseClient.getAuthService().loginWithIdToken(SupabaseClient.SUPABASE_ANON_KEY, authHeader, request)
                .enqueue(new Callback<AuthModels.AuthResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<AuthModels.AuthResponse> call, @NonNull Response<AuthModels.AuthResponse> response) {
                        btnGoogle.setEnabled(true);
                        if (response.isSuccessful() && response.body() != null) {
                            AuthModels.AuthResponse authResponse = response.body();
                            String name = "User";
                            String email = "";
                            String userId = "";
                            String profileUrl = "";
                            if (authResponse.user != null) {
                                userId = authResponse.user.id;
                                email = authResponse.user.email;
                                if (authResponse.user.userMetadata != null) {
                                    name = authResponse.user.userMetadata.fullName;
                                    profileUrl = authResponse.user.userMetadata.avatarUrl;
                                }
                            }
                            sessionManager.saveSession(authResponse.accessToken, authResponse.refreshToken, userId, email, name, profileUrl);
                            
                            syncUserToDatabase(userId, name, profileUrl, authResponse.accessToken);
                        } else {
                            String errorMsg = parseError(response);
                            Toast.makeText(AuthActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<AuthModels.AuthResponse> call, @NonNull Throwable t) {
                        btnGoogle.setEnabled(true);
                        Toast.makeText(AuthActivity.this, "Something went wrong. Please contact the developer.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void syncUserToDatabase(String userId, String name, String profileUrl, String accessToken) {
        java.util.Map<String, Object> userData = new java.util.HashMap<>();
        userData.put("uid", userId);
        userData.put("name", name);
        if (profileUrl != null && !profileUrl.isEmpty()) {
            userData.put("profile_url", profileUrl);
        }

        String authHeader = "Bearer " + accessToken;

        SupabaseClient.getApiService().addData(
                "Users",
                SupabaseClient.SUPABASE_ANON_KEY,
                authHeader,
                "resolution=merge-duplicates,return=representation",
                userData
        ).enqueue(new Callback<java.util.List<java.util.Map<String, Object>>>() {
            @Override
            public void onResponse(@NonNull Call<java.util.List<java.util.Map<String, Object>>> call, @NonNull Response<java.util.List<java.util.Map<String, Object>>> response) {
                if (response.isSuccessful()) {
                    android.util.Log.d("SupabaseSync", "User synced successfully");
                } else {
                    android.util.Log.e("SupabaseSync", "Sync failed: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            android.util.Log.e("SupabaseSync", "Error details: " + response.errorBody().string());
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                }
                startActivity(new Intent(AuthActivity.this, HomeActivity.class));
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<java.util.List<java.util.Map<String, Object>>> call, @NonNull Throwable t) {
                android.util.Log.e("SupabaseSync", "Network Error: " + t.getMessage());
                startActivity(new Intent(AuthActivity.this, HomeActivity.class));
                finish();
            }
        });
    }

    private void handleLogin() {
        String email = etLoginEmail.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();

        if (email.isEmpty()) {
            etLoginEmail.setError("Email is required");
            etLoginEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etLoginPassword.setError("Password is required");
            etLoginPassword.requestFocus();
            return;
        }

        setLoadingState(btnLogin, true, "Logging in...");
        String authHeader = "Bearer " + SupabaseClient.SUPABASE_ANON_KEY;
        AuthModels.LoginRequest request = new AuthModels.LoginRequest(email, password);
        
        SupabaseClient.getAuthService().login(SupabaseClient.SUPABASE_ANON_KEY, authHeader, request)
                .enqueue(new Callback<AuthModels.AuthResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<AuthModels.AuthResponse> call, @NonNull Response<AuthModels.AuthResponse> response) {
                        setLoadingState(btnLogin, false, "Login");
                        if (response.isSuccessful() && response.body() != null) {
                            AuthModels.AuthResponse authResponse = response.body();
                            String name = "User";
                            String email = "";
                            String userId = "";
                            String profileUrl = "";
                            if (authResponse.user != null) {
                                userId = authResponse.user.id;
                                email = authResponse.user.email;
                                if (authResponse.user.userMetadata != null) {
                                    name = authResponse.user.userMetadata.fullName;
                                    profileUrl = authResponse.user.userMetadata.avatarUrl;
                                }
                            }
                            sessionManager.saveSession(authResponse.accessToken, authResponse.refreshToken, userId, email, name, profileUrl);
                            
                            syncUserToDatabase(userId, name, profileUrl, authResponse.accessToken);
                        } else {
                            String errorMsg = parseError(response);
                            Toast.makeText(AuthActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<AuthModels.AuthResponse> call, @NonNull Throwable t) {
                        setLoadingState(btnLogin, false, "Login");
                        Toast.makeText(AuthActivity.this, "Something went wrong. Please contact the developer.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleRegister() {
        String name = etRegName.getText().toString().trim();
        String email = etRegEmail.getText().toString().trim();
        String password = etRegPassword.getText().toString().trim();
        String confirm = etRegConfirm.getText().toString().trim();

        if (name.isEmpty()) {
            etRegName.setError("Full name is required");
            etRegName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etRegEmail.setError("Email is required");
            etRegEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etRegEmail.setError("Please enter a valid email");
            etRegEmail.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etRegPassword.setError("Password must be at least 6 characters");
            etRegPassword.requestFocus();
            return;
        }

        if (!password.equals(confirm)) {
            etRegConfirm.setError("Passwords do not match");
            etRegConfirm.requestFocus();
            return;
        }

        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Please agree to the terms & conditions", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoadingState(btnRegister, true, "Creating account...");
        String authHeader = "Bearer " + SupabaseClient.SUPABASE_ANON_KEY;
        AuthModels.SignUpRequest request = new AuthModels.SignUpRequest(email, password, name);
        SupabaseClient.getAuthService().signUp(SupabaseClient.SUPABASE_ANON_KEY, authHeader, request)
                .enqueue(new Callback<AuthModels.AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthModels.AuthResponse> call, Response<AuthModels.AuthResponse> response) {
                        setLoadingState(btnRegister, false, "Register");
                        if (response.isSuccessful() && response.body() != null) {
                            AuthModels.AuthResponse authResponse = response.body();
                            
                            // If Supabase returns a session (email confirmation disabled), log in immediately
                            if (authResponse.accessToken != null) {
                                String name = "User";
                                String email = "";
                                String userId = "";
                                String profileUrl = "";
                                if (authResponse.user != null) {
                                    userId = authResponse.user.id;
                                    email = authResponse.user.email;
                                    if (authResponse.user.userMetadata != null) {
                                        name = authResponse.user.userMetadata.fullName;
                                        profileUrl = authResponse.user.userMetadata.avatarUrl;
                                    }
                                }
                                sessionManager.saveSession(authResponse.accessToken, authResponse.refreshToken, userId, email, name, profileUrl);
                                syncUserToDatabase(userId, name, profileUrl, authResponse.accessToken);
                                Toast.makeText(AuthActivity.this, "Welcome! Account created successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                // If no token, maybe email confirmation is still on in Supabase, but we'll just show login
                                Toast.makeText(AuthActivity.this, "Account created! Please login now.", Toast.LENGTH_LONG).show();
                                showLogin();
                            }
                        } else {
                            String errorMsg = parseError(response);
                            Toast.makeText(AuthActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthModels.AuthResponse> call, Throwable t) {
                        setLoadingState(btnRegister, false, "Register");
                        Toast.makeText(AuthActivity.this, "Something went wrong. Please contact the developer.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setLoadingState(View button, boolean isLoading, String text) {
        button.setEnabled(!isLoading);
        if (button instanceof MaterialButton) {
            ((MaterialButton) button).setText(text);
        }
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

        // Terms & Conditions (Register)
        String termsText = "I agree to the Terms & Conditions and Privacy Policy";
        SpannableString ssTerms = new SpannableString(termsText);
        
        ClickableSpan termsSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                // Handle Terms & Conditions click
            }
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        ssTerms.setSpan(termsSpan, 15, 33, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssTerms.setSpan(new ForegroundColorSpan(getColor(R.color.primary)), 15, 33, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        ssTerms.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                // Handle Privacy Policy click
            }
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        }, 38, ssTerms.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssTerms.setSpan(new ForegroundColorSpan(getColor(R.color.primary)), 38, ssTerms.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        cbTerms.setText(ssTerms);
        cbTerms.setMovementMethod(LinkMovementMethod.getInstance());
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
        TransitionManager.beginDelayedTransition(findViewById(android.R.id.content), transition);
    }

    private String parseError(Response<AuthModels.AuthResponse> response) {
        if (response.code() == 429) {
            return "Too many requests. Please wait a moment.";
        }
        try {
            if (response.errorBody() != null) {
                String errorJson = response.errorBody().string();
                AuthModels.AuthResponse errorResponse = new com.google.gson.Gson().fromJson(errorJson, AuthModels.AuthResponse.class);
                if (errorResponse != null) {
                    String desc = errorResponse.errorDescription != null ? errorResponse.errorDescription : errorResponse.error;
                    if (desc != null) {
                        if (desc.contains("Invalid login credentials")) return "Wrong email or password.";
                        if (desc.contains("User already registered")) return "This email is already registered.";
                        if (desc.contains("rate limit")) return "Please wait a moment and try again.";
                        return desc;
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return "Something went wrong. Please try again.";
    }
}

