package com.example.myapplication.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etUsername, etConfirmPass;
    private Button btnRegister, btnGoToLogin;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI elements
        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);
        etConfirmPass = findViewById(R.id.etConfirmPassword);
        etUsername = findViewById(R.id.etRegisterUsername);
        btnRegister = findViewById(R.id.btnRegister);
        btnGoToLogin = findViewById(R.id.btnGoToLogin);

        // Button click listeners
        btnRegister.setOnClickListener(v -> registerUser());

        btnGoToLogin.setOnClickListener(v -> {
            Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        });

        // Apply animation to buttons
        applyButtonClickAnimation(btnRegister);
        applyButtonClickAnimation(btnGoToLogin);
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

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPass = etConfirmPass.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        //make sure no fields are empty
        if (email.isEmpty() || password.isEmpty() || confirmPass.isEmpty() || username.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        //make sure password and password confirmation matches
        else if (!confirmPass.equals(password) ){
            Toast.makeText(RegisterActivity.this, "password doesn't match", Toast.LENGTH_SHORT).show();
            return;
        }
        //creates the user in firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserToFirestore(user.getUid(), email, username);
                        }
                    } else {
                        // If registration fails
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToFirestore(String uid, String email, String username) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("username", username);

        firestore.collection("users").document(uid)
                .set(userData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Save user info in SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("username", username);
                        editor.apply();

                        Toast.makeText(RegisterActivity.this, "Welcome " + username, Toast.LENGTH_SHORT).show();

                        // Go to HomeActivity
                        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Error saving user data.";
                        Toast.makeText(RegisterActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }
}