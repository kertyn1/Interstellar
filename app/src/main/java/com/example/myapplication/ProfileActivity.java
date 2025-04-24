package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {
    private ImageView imgProfile;
    private TextView tvUserName;
    private SharedPreferences sharedPreferences;
    private int[] profileImages = {
            R.drawable.saturn, R.drawable.earth, R.drawable.mars
    };
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvUserName = findViewById(R.id.tvUserName);
        imgProfile = findViewById(R.id.imgProfile);
        Button btnChangePic = findViewById(R.id.btnChangePic);
        Button btnLogout = findViewById(R.id.btnLogout);

        // Load from SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        currentIndex = sharedPreferences.getInt(uid + "_profilePicIndex", 0);
        imgProfile.setImageResource(profileImages[currentIndex]);

        // Load name from SharedPreferences instead of Firebase
        String username = sharedPreferences.getString("username", "User");
        tvUserName.setText("Hello, " + username);

        btnChangePic.setOnClickListener(v -> {
            currentIndex = (currentIndex + 1) % profileImages.length;
            imgProfile.setImageResource(profileImages[currentIndex]);
            sharedPreferences.edit().putInt(uid + "_profilePicIndex", currentIndex).apply();
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}