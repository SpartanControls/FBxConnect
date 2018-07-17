package com.spartancontrols.fbxconnect.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.spartancontrols.fbxconnect.R;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };



    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyStoragePermissions(this);
    }

    public void goToEFMActivity(View view) {
        Intent intent = new Intent(this, EFMActivity.class);
        startActivity(intent);
    }

    public void goToGasActivity(View view) {
        Intent intent = new Intent(this, GasActivity.class);
        startActivity(intent);
    }

    public void goToPlateActivity(View view) {
        Intent intent = new Intent(this, PlateActivity.class);
        startActivity(intent);
    }
}