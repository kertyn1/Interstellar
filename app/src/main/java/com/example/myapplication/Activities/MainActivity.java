package com.example.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.animation.ObjectAnimator;
import android.view.animation.BounceInterpolator;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get references to the buttons
        Button registerButton = findViewById(R.id.btnMainRegister);
        Button loginButton = findViewById(R.id.btnMainLogin);

        // Set onClick listeners for Register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        // Set onClick listeners for Login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        // Create an animation for when the buttons are pressed
        View.OnTouchListener scaleButtonTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Scale down
                    ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(v, "scaleX", 0.9f);
                    ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(v, "scaleY", 0.9f);
                    scaleDownX.setDuration(200); // Duration in milliseconds
                    scaleDownY.setDuration(200);
                    scaleDownX.start();
                    scaleDownY.start();
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    // Scale back to normal size
                    ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(v, "scaleX", 1f);
                    ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(v, "scaleY", 1f);
                    scaleUpX.setDuration(200);
                    scaleUpY.setDuration(200);
                    scaleUpX.setInterpolator(new BounceInterpolator()); //adds a bouncing effect when scaling back
                    scaleUpY.setInterpolator(new BounceInterpolator());
                    scaleUpX.start();
                    scaleUpY.start();
                }
                return false; // Let the view handle the touch event
            }
        };

        // Set the touch listener for both buttons
        registerButton.setOnTouchListener(scaleButtonTouchListener);
        loginButton.setOnTouchListener(scaleButtonTouchListener);
    }
}