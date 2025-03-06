package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class APODActivity extends AppCompatActivity {

    private TextView titleTextView, dateTextView, explanationTextView, selectedDateTextView;
    private ImageView imageView;
    private Button datePickerButton, backToHomeButton; // Added backToHomeButton
    private APODClient apodClient;
    private final Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_apodactivity);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.apod), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.nav);
        toolbar.inflateMenu(R.menu.tool_bar_menu);
        toolbar.setOnMenuItemClickListener(item -> handleMenuItemClick(item, this));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.nav), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        titleTextView = findViewById(R.id.titleTextView);
        dateTextView = findViewById(R.id.dateTextView);
        explanationTextView = findViewById(R.id.explanationTextView);
        imageView = findViewById(R.id.imageView);
        selectedDateTextView = findViewById(R.id.selectedDateTextView);
        datePickerButton = findViewById(R.id.datePickerButton);
        backToHomeButton = findViewById(R.id.backToHomeButton); // Initialize the back button

        apodClient = new APODClient();
        // Load today's APOD by default
        fetchAstronomyPictureOfTheDay(dateFormat.format(calendar.getTime()));

        // Handle date picker button click
        datePickerButton.setOnClickListener(v -> showDatePickerDialog());

        // Handle back to home button click
        backToHomeButton.setOnClickListener(v -> {
            startActivity(new Intent(APODActivity.this, HomeActivity.class)); // Navigate to HomeActivity
            finish(); // Optionally close the current activity
        });
    }

    private boolean handleMenuItemClick(MenuItem item, Context context) {
        if (context == null) return false;

        int itemId = item.getItemId();
        if (itemId == R.id.action_home) {
            startActivity(new Intent(APODActivity.this, HomeActivity.class));
            finish();
            return true;
        }
        else if (itemId == R.id.action_options) {
            startActivity(new Intent(APODActivity.this, SolarActivity.class));
            finish();
            return true;
        }
        else if (itemId == R.id.action_profile) {
            return true;
        }
        return false;
    }

    private void fetchAstronomyPictureOfTheDay(String date) {
        apodClient.getAstronomyPictureOfTheDay(date, new APODClient.APODResponseListener() {
            @Override
            public void onSuccess(APODResponse apodResponse) {
                titleTextView.setText(apodResponse.getTitle());
                dateTextView.setText(apodResponse.getDate());
                explanationTextView.setText(apodResponse.getExplanation());
                selectedDateTextView.setText("Selected Date: " + apodResponse.getDate());

                String imageUrl = apodResponse.getHdurl();
                if (imageUrl == null || imageUrl.isEmpty()) {
                    imageUrl = apodResponse.getUrl();
                }

                if (apodResponse.getMedia_type().equals("image")) {
                    Picasso.get().load(imageUrl).into(imageView);
                } else {
                    imageView.setImageResource(R.drawable.video_placeholder);
                }
            }

            @Override
            public void onError(String error) {
                Log.e("API Error", error);
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    String selectedDate = dateFormat.format(calendar.getTime());
                    fetchAstronomyPictureOfTheDay(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
}