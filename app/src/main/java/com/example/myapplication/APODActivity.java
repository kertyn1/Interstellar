package com.example.myapplication;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;

public class APODActivity extends AppCompatActivity {

    private TextView titleTextView, dateTextView, explanationTextView, selectedDateTextView;
    private ImageView imageView;
    private Button datePickerButton;
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
        titleTextView = findViewById(R.id.titleTextView);
        dateTextView = findViewById(R.id.dateTextView);
        explanationTextView = findViewById(R.id.explanationTextView);
        imageView = findViewById(R.id.imageView);
        selectedDateTextView = findViewById(R.id.selectedDateTextView);
        datePickerButton = findViewById(R.id.datePickerButton);

        apodClient = new APODClient();
        // Load today's APOD by default
        fetchAstronomyPictureOfTheDay(dateFormat.format(calendar.getTime()));

        // Handle date picker button click
        datePickerButton.setOnClickListener(v -> showDatePickerDialog());
    }
    // Initialize APODClient
    private void fetchAstronomyPictureOfTheDay(String date) {
        apodClient.getAstronomyPictureOfTheDay(date, new APODClient.APODResponseListener() {
            @Override
            public void onSuccess(APODResponse apodResponse) {
                titleTextView.setText(apodResponse.getTitle());
                dateTextView.setText(apodResponse.getDate());
                explanationTextView.setText(apodResponse.getExplanation());
                selectedDateTextView.setText("Selected Date: " + apodResponse.getDate());

                String imageUrl = apodResponse.getHdurl(); // High-quality image URL
                if (imageUrl == null || imageUrl.isEmpty()) {
                    imageUrl = apodResponse.getUrl(); // Fallback to standard URL
                }

                if (apodResponse.getMedia_type().equals("image")) {
                    Picasso.get().load(imageUrl).into(imageView);
                } else {
                    imageView.setImageResource(R.drawable.video_placeholder); // Placeholder for videos
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

        // Set max date to today to prevent future selections
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

}