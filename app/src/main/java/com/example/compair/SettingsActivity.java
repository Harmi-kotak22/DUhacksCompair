package com.example.compair;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    private LinearLayout layoutAppearance, layoutEditProfile, layoutRateApp;
    private TextView tvAboutUs, tvLogoutMessage;
    private Switch switchDarkMode;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize Views
        layoutAppearance = findViewById(R.id.layout_appearance);
        layoutEditProfile = findViewById(R.id.layout_edit_profile);
        layoutRateApp = findViewById(R.id.layout_rate_app);
        tvAboutUs = findViewById(R.id.tv_about_us);
        tvLogoutMessage = findViewById(R.id.tv_logout_message);
        switchDarkMode = findViewById(R.id.switch_dark_mode);

        Button btnCancelProfile = findViewById(R.id.btn_cancel_profile);
        Button btnSubmitRating = findViewById(R.id.btn_submit_rating);

        // Handle Cancel Button Click in Edit Profile Section
        btnCancelProfile.setOnClickListener(v -> toggleVisibility(layoutEditProfile));

        // Load SharedPreferences
        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("DarkMode", false);
        switchDarkMode.setChecked(isDarkMode);

        // Apply Dark Mode Preference
        //applyDarkMode(isDarkMode);

        // Button Click Events
     //   findViewById(R.id.btn_appearance).setOnClickListener(v -> toggleVisibility(layoutAppearance));
        findViewById(R.id.btn_edit_profile).setOnClickListener(v -> toggleVisibility(layoutEditProfile));
        findViewById(R.id.btn_rate_app).setOnClickListener(v -> toggleVisibility(layoutRateApp));
        findViewById(R.id.btn_about_us).setOnClickListener(v -> toggleVisibility(tvAboutUs));

        // Handle Logout Button Click
        findViewById(R.id.btn_logout).setOnClickListener(v -> logoutUser());

        // Dark Mode Toggle
//        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            sharedPreferences.edit().putBoolean("DarkMode", isChecked).apply();
//           // applyDarkMode(isChecked);
//        });

        // Handle Submit Rating Button Click
        btnSubmitRating.setOnClickListener(v -> {
            Toast.makeText(this, "Thank you for rating!", Toast.LENGTH_SHORT).show();
            toggleVisibility(layoutRateApp);
        });
    }

    private void toggleVisibility(View view) {
        view.setVisibility(view.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

//    private void applyDarkMode(boolean isEnabled) {
//        AppCompatDelegate.setDefaultNightMode(
//                isEnabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
//        );
//    }

    // Handle Logout Process
    private void logoutUser() {
        // Clear user session but keep settings
        sharedPreferences.edit().remove("UserSession").apply();

        // Navigate to LoginActivity
        Intent intent = new Intent(SettingsActivity.this, login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
