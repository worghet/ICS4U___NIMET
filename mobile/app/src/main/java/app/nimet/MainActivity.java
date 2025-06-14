package app.nimet;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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




















    TextView loadingTextView;



















    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
    double latitude, longitude;
    String currentCity;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    // api
    static final String NIMET_SERVER_ADDRESS = "18.218.44.44";
    static final String NIMET_DATA_API = "http://" + NIMET_SERVER_ADDRESS + ":8000/weather?location=";
    //

    Weather weatherData;
    Location locationData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // initialize activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // initialize views
        initializeViews();


        FrameLayout imageContainer = findViewById(R.id.imageContainer);

        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        ViewGroup.LayoutParams params = imageContainer.getLayoutParams();
        params.height = screenHeight;
        imageContainer.setLayoutParams(params);



//        // get current location, and get data for it.
        loadData(); //ahh!! i dont like this!! I would want this to be modular (loads and updates views)

        // initialize
//        getDataButton = findViewById(R.id.go_to_cloud);
//        dataText = findViewById(R.id.data_here);
//


    }

    private void initializeViews() {


        loadingTextView = findViewById(R.id.loadingTextView);

        ToggleButton unitToggle = findViewById(R.id.unit_toggle);
        TextView overview = findViewById(R.id.overview);

        unitToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                displayWeatherData(FAHRENHEIT);
//                overview.setText("CONDITION || ##°F");
            } else {
                displayWeatherData(CELSIUS);
//                overview.setText("CONDITION || ##°C");
            }
        });



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

            runOnUiThread(() -> loadingTextView.setText("Starting request to server..."));


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
                    JsonObject fullJson = JsonParser.parseString(jsonResponse).getAsJsonObject();
                    JsonObject reportJson = fullJson.getAsJsonObject("report");

                    locationData = new Gson().fromJson(reportJson.get("location"), Location.class);
                    weatherData = new Gson().fromJson(reportJson.get("weather"), Weather.class);






                    runOnUiThread(() -> {

                        // finished loading so hide the loading text
                       loadingTextView.setText("");


                        updateUI(fullJson.get("backgroundImage").getAsJsonObject().get("url").getAsString());

                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> loadingTextView.setText("FAILED TO ACCESS SERVER (" + requestedCity.toUpperCase() + ")"));
                System.out.println("failed: " + e.toString());
            }
        }).start();
    }

    private void updateUI(String bgURL) {

        // set image background
        FrameLayout imageContainer = findViewById(R.id.imageContainer);

        Glide.with(this)
                .load(bgURL)
                .override(1080, 2400) // set this to device dimensions using Resources.getSystem().getDisplayMetrics().heightPixels;
                .centerCrop()
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, Transition<? super Drawable> transition) {
                        imageContainer.setBackground(resource);
                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        // Optional: handle view recycling or cleanup here
                    }
                });


        // set location name
        ((TextView) findViewById(R.id.location_name)).setText(locationData.getCityName().toUpperCase() + " // " + locationData.getCountryName().toUpperCase());

        // set weather data
        displayWeatherData(CELSIUS);


    }

    final private int CELSIUS = 0;
    final private int FAHRENHEIT = 1;


    private void findAndLoadFrontsMap() {
        new Thread(() -> {
            java.time.ZonedDateTime currentTime = java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC);

            for (int i = 0; i < 60; i++) {
                java.time.ZonedDateTime attemptTime = currentTime.minusMinutes(i);
                String datePart = String.format("%04d%02d%02d",
                        attemptTime.getYear(),
                        attemptTime.getMonthValue(),
                        attemptTime.getDayOfMonth());

                String timePart = String.format("%02d%02d",
                        attemptTime.getHour(),
                        attemptTime.getMinute());

                String url = "https://s.w-x.co/staticmaps/wu/fee4c/surface_cur/conus/" + datePart + "/" + timePart + "z.jpg";

                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestMethod("HEAD");
                    connection.connect();

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        // Valid image found, load with Glide on UI thread
                        String finalUrl = url;
                        runOnUiThread(() -> {
                            ImageView frontsMap = findViewById(R.id.fronts_map);
                            Glide.with(this)
                                    .load(finalUrl)
                                    .into(frontsMap);
                        });
                        break;
                    }
                } catch (IOException ignored) {
                    // skip to next
                }
            }
        }).start();
    }


    private void displayWeatherData(int UNIT_OF_MEASUREMENT) {



        // start searching for fronts map
        findAndLoadFrontsMap();

        // overview
        ((TextView) findViewById(R.id.overview)).setText(weatherData.getCondition().toUpperCase() + " || " + Math.round(weatherData.getTemperature()[UNIT_OF_MEASUREMENT]) + Weather.UNIT_SYNTAX[UNIT_OF_MEASUREMENT][Weather.TEMP]);

        // set the temperatures (first row)
        ((TextView) findViewById(R.id.temperature)).setText(Math.round(weatherData.getTemperature()[UNIT_OF_MEASUREMENT]) + weatherData.UNIT_SYNTAX[UNIT_OF_MEASUREMENT][Weather.TEMP]);
        ((TextView) findViewById(R.id.temperature_feels_like)).setText(Math.round(weatherData.getFeels_like()[UNIT_OF_MEASUREMENT]) + weatherData.UNIT_SYNTAX[UNIT_OF_MEASUREMENT][Weather.TEMP]);

        // set the dew point
        ((TextView) findViewById(R.id.dew_point)).setText(Math.round(weatherData.getDew_point()[UNIT_OF_MEASUREMENT]) + weatherData.UNIT_SYNTAX[UNIT_OF_MEASUREMENT][Weather.TEMP]);

        // set the wind
        ((TextView) findViewById(R.id.wind)).setText(Math.round(weatherData.getWind_speed()[UNIT_OF_MEASUREMENT]) + weatherData.UNIT_SYNTAX[UNIT_OF_MEASUREMENT][Weather.SPEED]);
        ((TextView) findViewById(R.id.wind_descriptor)).setText("Wind (" + weatherData.getWind_heading() + ")");

        // pressure TODO some stuff should not be rounded..
        ((TextView) findViewById(R.id.pressure)).setText(String.valueOf(weatherData.getAir_pressure()[UNIT_OF_MEASUREMENT]));
        ((TextView) findViewById(R.id.pressure_descriptor)).setText("Pressure (" +  weatherData.UNIT_SYNTAX[UNIT_OF_MEASUREMENT][Weather.PRESSURE] + ")");

        // visibility
        ((TextView) findViewById(R.id.visibility)).setText(Math.round(weatherData.getVisibility()[UNIT_OF_MEASUREMENT]) + weatherData.UNIT_SYNTAX[UNIT_OF_MEASUREMENT][Weather.DISTANCE]);

        // percent based stuff;
        ((TextView) findViewById(R.id.humidity)).setText(Math.round(weatherData.getHumidity()) + "%");
        ((TextView) findViewById(R.id.cloud_coverage)).setText(Math.round(weatherData.getCloud_coverage()) + "%");


    }


    private void loadData() {
        if (isGPSEnabled()) {
            System.out.println("GPS ENABLED");

            loadingTextView.setText("Getting location..");

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

                    // ACTUAL PROCESS STARTS HERE

                    if (locationResult != null && !locationResult.getLocations().isEmpty()) {
                        int index = locationResult.getLocations().size() - 1;
                        try {
                            if (currentCity == null) {

                                currentCity = geocodeLatLong(locationResult.getLocations().get(index).getLatitude(), locationResult.getLocations().get(index).getLongitude());

                                loadingTextView.setText("You are in... " + currentCity + "... getting data...");

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
            }
        }
    }


    public void GO_TO_CLOUD_IDENTIFIER(View view) {
        Intent intent = new Intent(this, CloudIdentifierActivity.class);
        startActivity(intent);
//        intent.set
    }












}


