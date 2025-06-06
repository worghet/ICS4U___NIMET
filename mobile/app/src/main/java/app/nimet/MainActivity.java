package app.nimet;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Dictionary;
import java.util.Locale;

import app.nimet.utils.*;

public class MainActivity extends AppCompatActivity {

    Dictionary<Location, Weather> locationReports;

    // views
    Button getDataButton;
    TextView dataText;

    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
    double latitude, longitude;
    String currentCity;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    // api
    static final String NIMET_LOCAL_SERVER_ADDRESS = "192.168.100.53";
    static final String NIMET_DATA_API = "http://" + NIMET_LOCAL_SERVER_ADDRESS + ":8000/weather?location=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // initialize activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize
        getDataButton = findViewById(R.id.go_to_cloud);
        dataText = findViewById(R.id.data_here);

        // get current location, and get data for it.
        getAndUpdateCurrentLocationData(); //ahh!! i dont like this!! I would want this to be modular


    }

    String geocodeLatLong(double latitude, double longitude) throws IOException {
        System.out.println("geocoding >> lat: " + latitude + " >> long: " + longitude);
        System.out.println("result>>" + geocoder.getFromLocation(latitude, longitude, 1).get(0).getLocality());
        return geocoder.getFromLocation(latitude, longitude, 1).get(0).getLocality();
    }


    public void getData(String requestedCity) {
        new Thread(() -> {
            System.out.println("city --->> " + requestedCity);
            String urlString = NIMET_DATA_API + requestedCity;
            System.out.println("url string --> " + urlString);

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

                    // ⚠️ UI update must happen on the main thread
                    runOnUiThread(() -> dataText.setText(jsonResponse));
                } else {
                    System.out.println("cant get data");
                    runOnUiThread(() -> dataText.setText("sum server issue " + requestedCity));
                }
            } catch (Exception e) {
                runOnUiThread(() -> dataText.setText("error!! cant get " + requestedCity));
                System.out.println("failed: " + e.toString());
            }
        }).start();
    }

    private void getAndUpdateCurrentLocationData() {
        if (isGPSEnabled()) {
            System.out.println("GPS ENABLED");

            // location request stuffs
            LocationRequest locationRequest = LocationRequest.create();
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
                        try {
                            if (currentCity == null) {

                                currentCity = geocodeLatLong(locationResult.getLocations().get(index).getLatitude(), locationResult.getLocations().get(index).getLongitude());
                                getData(currentCity);
                                System.out.println("current ciy is NOW -->>>" + currentCity);
                            }
                        } catch (IOException e) {
                            System.out.println("oops!");
                        }
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
//                updateCurrentLocation();
//                getData(currentCity);
            } else {
                dataText.setText("Location permission denied.");
            }
        }
    }


    public void GO_TO_CLOUD_IDENTIFIER(View view) {
        Intent intent = new Intent(this, CloudIdentifierActivity.class);
        startActivity(intent);
//        intent.set
    }

}
