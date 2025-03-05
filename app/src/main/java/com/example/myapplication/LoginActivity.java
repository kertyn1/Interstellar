package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.MotionEvent;
import android.view.View;
import android.animation.ObjectAnimator;
import android.view.animation.BounceInterpolator;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private Button btnGoToRegister;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI elements
        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);

        // Apply button animation to all buttons
        applyButtonClickAnimation(btnLogin);
        applyButtonClickAnimation(btnGoToRegister);

        // Button click listener to log in the user
        btnLogin.setOnClickListener(v -> loginUser());

        btnGoToRegister.setOnClickListener(v -> {
            Intent loginIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(loginIntent);
        });
    }

    private void applyButtonClickAnimation(Button button) {
        button.setOnTouchListener((v, event) -> {
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
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Fetch user data from Firestore
                            firestore.collection("users").document(user.getUid())
                                    .get()
                                    .addOnCompleteListener(dataTask -> {
                                        if (dataTask.isSuccessful()) {
                                            DocumentSnapshot document = dataTask.getResult();
                                            if (document.exists()) {
                                                // Successfully logged in
                                                Toast.makeText(LoginActivity.this, "Welcome " + document.getString("username"), Toast.LENGTH_SHORT).show();
                                                // Redirect to HomeActivity
                                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                                finish();
                                            }
                                        }
                                    });
                        }
                    } else {
                        // If login fails
                        Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
