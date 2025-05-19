package com.example.myapplication.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.animation.ObjectAnimator;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.SharedPreferences;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.ToolbarUtils;

import java.lang.reflect.Method;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TextView tvWelcome = findViewById(R.id.welcomeText);

// Get the stored username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Explorer");

// Update the welcome text
        tvWelcome.setText("Welcome to Interstellar, " + username + "!");

        Toolbar toolbar = findViewById(R.id.nav);
        toolbar.inflateMenu(R.menu.tool_bar_menu);
        ToolbarUtils.applyProfileIconToToolbar(toolbar, this); // ✅ Apply custom profile icon
        toolbar.setOnMenuItemClickListener(item -> handleMenuItemClick(item, this));

        Button btnAPOD = findViewById(R.id.btnAPOD);
        Button btnSolar = findViewById(R.id.btnSolar);

        // Set onClick listeners for navigation
        btnAPOD.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, APODActivity.class));
        });

        btnSolar.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, SolarActivity.class));
        });

        //animation for button touch
        View.OnTouchListener scaleButtonTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Scale down
                    ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(v, "scaleX", 0.9f);
                    ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(v, "scaleY", 0.9f);
                    scaleDownX.setDuration(200);
                    scaleDownY.setDuration(200);
                    scaleDownX.start();
                    scaleDownY.start();
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    // Scale back to normal size
                    ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(v, "scaleX", 1f);
                    ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(v, "scaleY", 1f);
                    scaleUpX.setDuration(200);
                    scaleUpY.setDuration(200);
                    scaleUpX.setInterpolator(new BounceInterpolator());
                    scaleUpY.setInterpolator(new BounceInterpolator());
                    scaleUpX.start();
                    scaleUpY.start();
                }
                return false;
            }
        };

        // Apply the touch listener to both buttons
        btnAPOD.setOnTouchListener(scaleButtonTouchListener);
        btnSolar.setOnTouchListener(scaleButtonTouchListener);
    }

    private boolean handleMenuItemClick(MenuItem item, Context context) {
        if (context == null) return false;

        int itemId = item.getItemId();
        if (itemId == R.id.action_home) {
            return true;
        }
        else if (itemId == R.id.action_solar) {
            startActivity(new Intent(HomeActivity.this, SolarActivity.class));
            finish();
            return true;
        }
        else if (itemId == R.id.action_apod) {
            startActivity(new Intent(HomeActivity.this, APODActivity.class));
            finish();
            return true;
        }
        else if (itemId == R.id.action_profile) {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            finish();
            return true;
        }

        return false;
    }
}