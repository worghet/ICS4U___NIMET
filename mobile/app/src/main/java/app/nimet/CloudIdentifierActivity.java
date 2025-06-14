package app.nimet;

// Imports.

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;
import android.os.Bundle;
import android.view.View;
import android.Manifest;
import java.util.Random;


/**
 * This is the logic for the {@code activity_cloud_identifier.xml}.
 * Contains functions for accessing camera, and getting an AI prediction from the server.
 */
public class CloudIdentifierActivity extends AppCompatActivity {


    // Constants.


    private static final int CAMERA_PERMISSION_CODE = 100;


    // Instance variables.


    private TextView cloud_description;
    private ImageView pictureResult;
    private Bitmap capturedImage;
    private TextView cloud_name;


    // OnCreate method.

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Default layout creation.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_identifier);

        // Initialize views
        pictureResult = findViewById(R.id.resulting_image);
        cloud_name = findViewById(R.id.cloud_name);
        cloud_description = findViewById(R.id.cloud_description);
    }

    // Method to get a cloud prediction (mocked for now)
    public void getPrediction(View view) {

        if (capturedImage == null) {
            return;
        }

        Random random = new Random();
        String predictionInQuotations = "nun"; // Default mock value
        String descriptionInQuotations = "nun"; // Default mock value

        switch (random.nextInt(2)) {
            case 0:
                predictionInQuotations = "cumulus";
                descriptionInQuotations = "puffy, can become a storm cloud.. Fun to look at!";
                break;
            case 1:
                predictionInQuotations = "stratus";
                descriptionInQuotations = "flat, and kinda boring!";

                break;
        }

        cloud_name.setText(predictionInQuotations);  // Set the cloud prediction text
        cloud_description.setText("\n" + descriptionInQuotations);
    }





    // Methods called by views in the activity.


    // Method to take a photo.
    public void TAKE_PHOTO(View view) {

        // Check if the permission has been granted.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            // Open the camera.
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraResultLauncher.launch(intent);

        }

        // If the permission was NOT yet granted, ask for it.
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }

    }

    // Method to go back to the main activity
    public void BACK_TO_MAIN(View view) {
        finish();
    }


    // Methods called as bi-processes.


    // Declare ActivityResultLauncher for camera result
    private ActivityResultLauncher<Intent> cameraResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    capturedImage = imageBitmap;
                    pictureResult.setImageBitmap(imageBitmap);  // Display the captured image
                    // You can now send the image to the backend for processing if needed
                }
            });


    // Method to handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraResultLauncher.launch(intent);

            } else {
                // Permission denied
                Toast.makeText(this, "Camera permission is required to take a photo", Toast.LENGTH_SHORT).show();
            }
        }
    }


}