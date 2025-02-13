package com.example.compair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class splash extends Activity {

    private static final int SPLASH_TIME_OUT = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(
                R.layout.activity_splash);

        ImageView logo = findViewById(R.id.logo);

        // Load image as circular using Glide
        Glide.with(this)
                .load(R.drawable.compairlogo) // Replace with your actual image
                .apply(RequestOptions.circleCropTransform()) // Make it circular
                .into(logo);

        // Load animations
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);

        // Apply animations
        logo.startAnimation(fadeIn);
        logo.startAnimation(bounce);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(splash.this, home.class);
            startActivity(intent);
            finish();
        }, SPLASH_TIME_OUT);
    }
}
