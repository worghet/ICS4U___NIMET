package app.nimet;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CloudIdentifierActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Initialize activity.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_identifier);

        // Initialize views.



    }

    public void takePhoto() {

    }

    public void getPrediction() {

    }

    public void backToMain(View view) {

        finish();

    }
}