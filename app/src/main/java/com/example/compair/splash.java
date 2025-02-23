package com.example.compair;

import static com.example.compair.R.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class splash extends Activity {

    private static final int SPLASH_TIME_OUT = 500; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(
                R.layout.activity_splash);

        ImageView logo = findViewById(R.id.logo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.status_bar_color));
        }
        // Load image as circular using Glide
        Glide.with(this)
                .load(drawable.compair) // Replace with your actual image
                .apply(RequestOptions.circleCropTransform()) // Make it circular
                .into(logo);

        // Load animations
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        // Apply animations
        logo.startAnimation(fadeIn) ;

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(splash.this, home.class);
            startActivity(intent);
            finish();
        }, SPLASH_TIME_OUT);
    }
}
