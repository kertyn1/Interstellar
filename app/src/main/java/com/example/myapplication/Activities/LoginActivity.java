package com.example.myapplication.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MotionEvent;
import android.animation.ObjectAnimator;
import android.view.animation.BounceInterpolator;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnGoToRegister;

    private TextView btnForgotPass;
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


        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);
        btnForgotPass = findViewById(R.id.btnForgotPass);

        // Apply button animation to all buttons
        applyButtonClickAnimation(btnLogin);
        applyButtonClickAnimation(btnGoToRegister);

        //log in the user
        btnLogin.setOnClickListener(v -> loginUser());
        //Forgot Password
        btnForgotPass.setOnClickListener(v -> resetPassword());
        //Moving to register screen
        btnGoToRegister.setOnClickListener(v -> {
            Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(registerIntent);
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
                            firestore.collection("users").document(user.getUid())
                                    .get()
                                    .addOnCompleteListener(dataTask -> {
                                        if (dataTask.isSuccessful()) {
                                            DocumentSnapshot document = dataTask.getResult();
                                            if (document.exists()) {
                                                // Successfully logged in
                                                String username = document.getString("username");
                                                Toast.makeText(LoginActivity.this, "Welcome " + document.getString("username"), Toast.LENGTH_SHORT).show();
                                                //saves user in SharedPreferences
                                                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putBoolean("isLoggedIn", true);
                                                editor.putString("username", username);
                                                editor.apply();
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

    private void resetPassword() {
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Enter your email to reset password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
