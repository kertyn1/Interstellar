package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    private int seconds = 0;
    private TextView tvTimer;
    private Button btnStartTimer;
    private boolean isTimerRunning = false;
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isTimerRunning) {
                seconds++;
                tvTimer.setText("Time: " + seconds + "s");
                handler.postDelayed(this, 1000); // Update every second
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        // Set up the layout for edge-to-edge support
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar toolbar = findViewById(R.id.nav);
        toolbar.inflateMenu(R.menu.tool_bar_menu);
        toolbar.setOnMenuItemClickListener(item -> handleMenuItemClick(item, this));

        // Initialize views
        tvTimer = findViewById(R.id.tvTimer);
        btnStartTimer = findViewById(R.id.btnStartTimer);

        // Button to start/stop timer
        btnStartTimer.setOnClickListener(v -> toggleTimer());

        // Set the animation for button touch
        btnStartTimer.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Scale down
                v.setScaleX(0.9f);
                v.setScaleY(0.9f);
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // Scale back to normal size
                v.setScaleX(1f);
                v.setScaleY(1f);
            }
            return false;
        });
    }

    private boolean handleMenuItemClick(MenuItem item, Context context) {
        if (context == null) return false;

        int itemId = item.getItemId();
        if (itemId == R.id.action_home) {
            startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
            finish();
            return true;
        }
        else if (itemId == R.id.action_options) {
            return true;
        }
        else if (itemId == R.id.action_profile) {
            startActivity(new Intent(SettingsActivity.this, ProfileActivity.class));
            finish();
            return true;
        }

        return false;
    }

    // Toggle the timer on/off when the button is pressed
    private void toggleTimer() {
        if (isTimerRunning) {
            // Stop the timer
            isTimerRunning = false;
            btnStartTimer.setText("Start Timer");
        } else {
            // Start the timer
            isTimerRunning = true;
            btnStartTimer.setText("Stop Timer");
            handler.post(timerRunnable); // Start the timer runnable
        }
    }
}