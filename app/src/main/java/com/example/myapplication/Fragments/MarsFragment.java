package com.example.myapplication.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

public class MarsFragment extends Fragment {

    private TextView tvPlanetName, tvPlanetInfo;
    private ImageView planetImageView;
    private RequestQueue requestQueue;

    public MarsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mars, container, false);

        tvPlanetName = view.findViewById(R.id.tvPlanetName);
        tvPlanetInfo = view.findViewById(R.id.tvPlanetInfo);
        planetImageView = view.findViewById(R.id.planetImageView);
        tvPlanetName.setText("Mars");
        planetImageView.setImageResource(R.drawable.mars); // Set Mars image

        requestQueue = Volley.newRequestQueue(requireContext());
        fetchPlanetData(tvPlanetInfo);

        return view;
    }

    private void fetchPlanetData(TextView tvPlanetInfo) {
        String url = "https://api.le-systeme-solaire.net/rest/bodies/mars";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject massObj = response.getJSONObject("mass");
                        double massValue = massObj.getDouble("massValue");
                        int massExponent = massObj.getInt("massExponent");
                        double gravity = response.getDouble("gravity");
                        double distanceFromSun = response.getDouble("semimajorAxis"); // in km

                        String infoText =
                                "Mass: " + massValue + " ×10^" + massExponent + " kg\n" +
                                        "Gravity: " + gravity + " m/s²\n" +
                                        "Distance from Sun: " + String.format("%,.0f", distanceFromSun) + " km";

                        tvPlanetInfo.setText(infoText);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        tvPlanetInfo.setText("Failed to load planet data.");
                    }
                },
                error -> {
                    error.printStackTrace();
                    tvPlanetInfo.setText("Failed to load planet data.");
                }
        );

        requestQueue.add(jsonObjectRequest);
    }
}