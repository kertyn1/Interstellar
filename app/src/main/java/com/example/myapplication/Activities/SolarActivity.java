package com.example.myapplication.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Fragments.DefaultFragment;
import com.example.myapplication.Fragments.EarthFragment;
import com.example.myapplication.Fragments.JupiterFragment;
import com.example.myapplication.Fragments.MarsFragment;
import com.example.myapplication.Fragments.MercuryFragment;
import com.example.myapplication.Fragments.NeptuneFragment;
import com.example.myapplication.R;
import com.example.myapplication.Fragments.SaturnFragment;
import com.example.myapplication.Fragments.UranusFragment;
import com.example.myapplication.Fragments.VenusFragment;

public class SolarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_solar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.nav);
        toolbar.inflateMenu(R.menu.tool_bar_menu);
        toolbar.setOnMenuItemClickListener(item -> handleMenuItemClick(item, this));

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new DefaultFragment())
                    .commit();
        }

        Button buttonNeptune = findViewById(R.id.fragment_button_Neptune);
        Button buttonUranus = findViewById(R.id.fragment_button_Uranus);
        Button buttonSaturn = findViewById(R.id.fragment_button_Saturn);
        Button buttonJupiter = findViewById(R.id.fragment_button_Jupiter);
        Button buttonMars = findViewById(R.id.fragment_button_Mars);
        Button buttonEarth = findViewById(R.id.fragment_button_Earth);
        Button buttonVenus = findViewById(R.id.fragment_button_Venus);
        Button buttonMercury = findViewById(R.id.fragment_button_Mercury);

        View.OnClickListener buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = null;

                if (v.getId() == R.id.fragment_button_Neptune) {
                    fragment = new NeptuneFragment();
                } else if (v.getId() == R.id.fragment_button_Uranus) {
                    fragment = new UranusFragment();
                } else if (v.getId() == R.id.fragment_button_Saturn) {
                    fragment = new SaturnFragment();
                } else if (v.getId() == R.id.fragment_button_Jupiter) {
                    fragment = new JupiterFragment();
                } else if (v.getId() == R.id.fragment_button_Mars) {
                    fragment = new MarsFragment();
                } else if (v.getId() == R.id.fragment_button_Earth) {
                    fragment = new EarthFragment();
                } else if (v.getId() == R.id.fragment_button_Venus) {
                    fragment = new VenusFragment();
                } else if (v.getId() == R.id.fragment_button_Mercury) {
                    fragment = new MercuryFragment();
                }

                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(
                                    android.R.anim.fade_in,
                                    android.R.anim.fade_out,
                                    android.R.anim.fade_in,
                                    android.R.anim.fade_out
                            )
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null) // allows user to press back
                            .commit();
                }
            }
        };

        buttonNeptune.setOnClickListener(buttonClickListener);
        buttonUranus.setOnClickListener(buttonClickListener);
        buttonSaturn.setOnClickListener(buttonClickListener);
        buttonJupiter.setOnClickListener(buttonClickListener);
        buttonMars.setOnClickListener(buttonClickListener);
        buttonEarth.setOnClickListener(buttonClickListener);
        buttonVenus.setOnClickListener(buttonClickListener);
        buttonMercury.setOnClickListener(buttonClickListener);
    }

    private boolean handleMenuItemClick(MenuItem item, Context context) {
        if (context == null) return false;

        int itemId = item.getItemId();
        if (itemId == R.id.action_home) {
            startActivity(new Intent(SolarActivity.this, HomeActivity.class));
            finish();
            return true;
        }
        if (itemId == R.id.action_apod) {
            startActivity(new Intent(SolarActivity.this, APODActivity.class));
            finish();
            return true;
        }
        else if (itemId == R.id.action_solar) {
            return true;
        }
        else if (itemId == R.id.action_profile) {
            startActivity(new Intent(SolarActivity.this, ProfileActivity.class));
            finish();
            return true;
        }

        return false;
    }
}
