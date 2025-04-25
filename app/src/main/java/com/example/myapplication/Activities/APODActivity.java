package com.example.myapplication.Activities;

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

import com.example.myapplication.APODClient;
import com.example.myapplication.APODResponse;
import com.example.myapplication.R;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.speech.tts.TextToSpeech;
import java.util.Locale;

public class APODActivity extends AppCompatActivity {

    private TextView titleTextView, dateTextView, explanationTextView, selectedDateTextView;
    private ImageView imageView;
    private TextToSpeech textToSpeech;
    private Button datePickerButton, backToHomeButton, readButton;
    private APODClient apodClient;
    private final Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private boolean isSpeaking = false;
    private String textToRead = "";

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



        titleTextView = findViewById(R.id.titleTextView);
        dateTextView = findViewById(R.id.dateTextView);
        explanationTextView = findViewById(R.id.explanationTextView);
        imageView = findViewById(R.id.imageView);
        selectedDateTextView = findViewById(R.id.selectedDateTextView);
        datePickerButton = findViewById(R.id.datePickerButton);
        backToHomeButton = findViewById(R.id.backToHomeButton);
        readButton = findViewById(R.id.readButton);

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

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported");
                } else {
                    // Section 5: set OnUtteranceProgressListener here
                    textToSpeech.setOnUtteranceProgressListener(new android.speech.tts.UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                            // Nothing needed when starting
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            runOnUiThread(() -> {
                                isSpeaking = false;
                                readButton.setText("Read Aloud");
                            });
                        }

                        @Override
                        public void onError(String utteranceId) {
                            runOnUiThread(() -> {
                                isSpeaking = false;
                                readButton.setText("Read Aloud");
                            });
                        }
                    });
                }
            } else {
                Log.e("TTS", "Initialization failed");
            }
        });

        readButton.setOnClickListener(v -> {
            if (isSpeaking) {
                pauseSpeaking();
            } else {
                startSpeaking();
            }
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
        else if (itemId == R.id.action_apod) {
            return true;
        }
        else if (itemId == R.id.action_solar) {
            startActivity(new Intent(APODActivity.this, SolarActivity.class));
            finish();
            return true;
        }
        else if (itemId == R.id.action_profile) {
            startActivity(new Intent(APODActivity.this, ProfileActivity.class));
            finish();
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
    private void startSpeaking() {
        if (textToSpeech != null) {
            textToRead = titleTextView.getText().toString() + ". "
                    + dateTextView.getText().toString() + ". "
                    + explanationTextView.getText().toString();

            textToSpeech.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, "TTS_ID");
            isSpeaking = true;
            readButton.setText("Pause");
        }
    }
    private void pauseSpeaking() {
        if (textToSpeech != null && textToSpeech.isSpeaking()) {
            textToSpeech.stop(); // stop immediately
        }
        isSpeaking = false;
        readButton.setText("Read Aloud");
    }
    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}