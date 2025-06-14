package app.nimet;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Random;

public class CloudIdentifierActivity extends AppCompatActivity {














    private static final int CAMERA_PERMISSION_CODE = 100;

    private ImageView pictureResult;
    private TextView identifiedLabel;
    private Bitmap capturedImage;

    // Declare ActivityResultLauncher for camera result
    private ActivityResultLauncher<Intent> cameraResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    capturedImage = imageBitmap;
                    pictureResult.setImageBitmap(imageBitmap);  // Display the captured image
                    // You can now send the image to the backend for processing if needed
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_identifier);

        // Initialize views
        pictureResult = findViewById(R.id.picture_result);
        identifiedLabel = findViewById(R.id.identifiedThing);
    }

    // Method to take a photo
    public void TAKE_PHOTO(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // If permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            // If permission is already granted, launch the camera
            launchCamera();
        }
    }

    // Method to handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open camera
                launchCamera();
            } else {
                // Permission denied
                Toast.makeText(this, "Camera permission is required to take a photo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Launch the camera to capture a photo
    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraResultLauncher.launch(intent); // Launch the camera using the new ActivityResultContracts API
    }

    // Method to get a cloud prediction (mocked for now)
    public void getPrediction(View view) {
        Random random = new Random();
        String predictionInQuotations = "nun"; // Default mock value

        switch (random.nextInt(2)) {
            case 0:
                predictionInQuotations = "cumulus";
                break;
            case 1:
                predictionInQuotations = "stratus";
                break;
        }

        identifiedLabel.setText(predictionInQuotations);  // Set the cloud prediction text
    }

    // Method to go back to the main activity
    public void backToMain(View view) {
        finish();  // Close this activity and go back to the previous screen
    }
}