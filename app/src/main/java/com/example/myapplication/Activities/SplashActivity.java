package com.example.myapplication.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends Activity {
    private static final int SPLASH_TIMEOUT = 2000; // 2 seconds

    Animation splashAnim;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sign out user so that app always starts logged out.
        FirebaseAuth.getInstance().signOut();

        // Make the window fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        splashAnim = AnimationUtils.loadAnimation(this, R.anim.splash_animation);
        image = findViewById(R.id.imageView);
        image.setAnimation(splashAnim);

        // Delay and then launch MainActivity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_TIMEOUT);
    }
}