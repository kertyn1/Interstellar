package com.example.myapplication.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {
    private ImageView imgProfile;
    private TextView tvUserName;
    private SharedPreferences sharedPreferences;
    private int[] profileImages = {
            R.drawable.mercury, R.drawable.venus, R.drawable.earth, R.drawable.mars, R.drawable.jupiter, R.drawable.saturn, R.drawable.uranus, R.drawable.neptune
    };
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.nav);
        toolbar.inflateMenu(R.menu.tool_bar_menu);
        toolbar.setOnMenuItemClickListener(item -> handleMenuItemClick(item, this));

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
            // Sign out from Firebase
            FirebaseAuth.getInstance().signOut();

            // Clear saved user data
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            // Go back to login/register screen
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
    private boolean handleMenuItemClick(MenuItem item, Context context) {
        if (context == null) return false;

        int itemId = item.getItemId();
        if (itemId == R.id.action_home) {
            startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            finish();
            return true;
        } else if (itemId == R.id.action_apod) {
            startActivity(new Intent(ProfileActivity.this, APODActivity.class));
            finish();
            return true;
        }
        else if (itemId == R.id.action_solar) {
            startActivity(new Intent(ProfileActivity.this, SolarActivity.class));
            finish();
            return true;
        }
        else if (itemId == R.id.action_profile) {
            return true;
        }

        return false;
    }
}