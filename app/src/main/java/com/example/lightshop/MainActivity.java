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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private TextView tvTitle, tvSubtitle;
    private LinearLayout loginContainer, registerContainer;
    private ViewGroup rootLayout;
    private TextView tvFooterLogin, tvFooterRegister;
    private CheckBox cbTerms;
    private View btnLogin, btnRegister;

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

        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
        });

        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
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
}