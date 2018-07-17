package com.spartancontrols.fbxconnect.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.spartancontrols.fbxconnect.R;

public class GasActivity extends AppCompatActivity {

    // Current station the user has selected
    private String selectedStation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas);

        // Set default selected station
        selectedStation = "stationAll";

        // Disable Meter 1 ad n2 buttons to toggle until the correct Station has been selected
        RadioButton rb = findViewById(R.id.rbMeter1);
        rb.setEnabled(false);

        rb = findViewById(R.id.rbMeter2);
        rb.setEnabled(false);

        // Add function for when radioGroup Button is selected
        RadioGroup radioStation = findViewById(R.id.rgStation);
        radioStation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedID) {
                stationSelection(checkedID);
            }
        });
    }

    /**
     * Add to a button to go back to the main menu
     * Clears all active activities from the screen and refreshes the app
     * @param view - current view
     */
    public void goToMainActivity(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Add to a button to go to the View Report page
     * TODO: dnp3 request
     * @param view - current view
     */
    public void goToReportActivity(View view) {
        Intent intent = new Intent(this, ReportActivity.class);
        intent.putExtra("station", selectedStation);
        intent.putExtra("report", "gas");
        startActivity(intent);
    }

    /**
     * Changes the selected station and toggles the possible meters that can be pressed
     * Sets the selectedStation string to the newly pressed radioButton
     * @param checkedID - int ID of selected station in radioGroup
     */
    private void stationSelection(int checkedID) {
        RadioButton radioButton;

        switch (checkedID) {
            // When All stations is selected
            // Disable meters 1 and 2 and toggle All meters on
            case R.id.rbStationAll:
                selectedStation = "stationAll";
                radioButton = findViewById(R.id.rbMeterAll);
                radioButton.setEnabled(true);
                radioButton.toggle();
                radioButton = findViewById(R.id.rbMeter1);
                radioButton.setEnabled(false);
                radioButton = findViewById(R.id.rbMeter2);
                radioButton.setEnabled(false);
                break;
            // When Station 1 is selected
            // Disable All meters and meter 2
            case R.id.rbStation1:
                selectedStation = "station1";
                radioButton = findViewById(R.id.rbMeterAll);
                radioButton.setEnabled(false);
                radioButton = findViewById(R.id.rbMeter1);
                radioButton.setEnabled(true);
                radioButton.toggle();
                radioButton = findViewById(R.id.rbMeter2);
                radioButton.setEnabled(false);
                break;
            // When Station 2 is selected
            // Disable All meters and meter 1
            default:
                selectedStation = "station2";
                radioButton = findViewById(R.id.rbMeterAll);
                radioButton.setEnabled(false);
                radioButton = findViewById(R.id.rbMeter1);
                radioButton.setEnabled(false);
                radioButton = findViewById(R.id.rbMeter2);
                radioButton.setEnabled(true);
                radioButton.toggle();
                break;
        }
    }
}
