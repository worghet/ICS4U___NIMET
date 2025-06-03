package app.nimet;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    Button getDataButton;
    TextView dataText;
    EditText city_entry;
    static final String NIMET_DATA_API = "http://10.0.0.213:8000/weather?location=";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Initialize Activity.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get Views.
        getDataButton = findViewById(R.id.get_data_btn);
        dataText = findViewById(R.id.data_here);
        city_entry = findViewById(R.id.city_enter);

    }


    public void getData(View view) {
        Log.d("MainActivity", "buttonPressed");


        // read entered city
        String city = city_entry.getText().toString().trim();

        // if empty, say so
        if (city.isEmpty()) {
            dataText.setText("Please enter a city name.");
            return;
        }

        // compile url
        String urlString = NIMET_DATA_API + city;

        // get data
        System.out.println("starting fetch");
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
                    System.out.println(jsonResponse);

                    runOnUiThread(() -> {
                        dataText.setText(jsonResponse);
                    });
                }
            } catch (Exception e) {
                System.out.println("failed: " + e.toString());
            }
        }).start();


        // show data


    }

}