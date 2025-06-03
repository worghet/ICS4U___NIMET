package app.nimet;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // views
    Button getDataButton;
    TextView dataText;
    EditText city_entry;

    // lat / long
    double latitude, longitude;
    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
    String requestedCity;

    // location request stuffs
    private LocationRequest locationRequest;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    // api
    static final String NIMET_DATA_API = "http://10.0.0.213:8000/weather?location=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // initialize activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize
        getDataButton = findViewById(R.id.get_data_btn);
        dataText = findViewById(R.id.data_here);
        city_entry = findViewById(R.id.city_enter);

        // make location request
        System.out.println("getting location");
        getLocation();



    }

    void geocodeLatLong() {
        new Thread(() -> {
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            try {
                System.out.println(latitude + " " + longitude);
                String city = geocoder.getFromLocation(latitude, longitude, 1)
                        .get(0).getLocality();
                System.out.println("city: " + city);

                // If you want to update the UI:
                runOnUiThread(() -> dataText.setText("City: " + city));
                requestedCity = city;

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                runOnUiThread(() -> dataText.setText("Error getting city"));
            }
        }).start();
    }


    public void getData(View view) {

        System.out.println("city --->> " + requestedCity);
        String urlString = NIMET_DATA_API + requestedCity;
        System.out.println("url string --> " + urlString);

        new Thread(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    String jsonResponse = response.toString();
                    runOnUiThread(() -> dataText.setText(jsonResponse));
                }
            } catch (Exception e) {
                System.out.println("failed: " + e.toString());
            }
        }).start();
    }

    public void getLocation() {
        if (isGPSEnabled()) {
            System.out.println("GPS ENABLED");

            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(5000);


            // Check location permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // Request permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
                return;
            }

            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    LocationServices.getFusedLocationProviderClient(MainActivity.this).removeLocationUpdates(this);

                    if (locationResult != null && !locationResult.getLocations().isEmpty()) {
                        int index = locationResult.getLocations().size() - 1;
                        latitude = locationResult.getLocations().get(index).getLatitude();
                        longitude = locationResult.getLocations().get(index).getLongitude();
                        geocodeLatLong();
                        dataText.setText("lat: " + latitude + "\nlon: " + longitude);
                    }
                }
            }, Looper.getMainLooper());

        } else {
            System.out.println("GPS DISABLED");
        }

    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    // Handle the permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, try again
                getLocation();
                geocodeLatLong();
            } else {
                dataText.setText("Location permission denied.");
            }
        }
    }
}
