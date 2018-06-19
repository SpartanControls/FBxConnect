package com.spartancontrols.fbxconnect;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.spartancontrols.InputFilterMinMax;

public class PlateActivity extends AppCompatActivity implements View.OnClickListener{

    Button button;

    ConstraintLayout cl;

    // Current index layout position to display
    //      0: Choose Meter
    //      1: Flow Conditions
    //      2: Shut-in
    //      3: First Measurement
    //      4: Confirm attempt to change orifice plate
    //      5: Enter new orifice plate size
    //      6: Second Measurement
    //      7: Maintenance Complete
    private int position;

    // Tracks if the maintenance layout came from either
    // the shut-in layout or flow layout for the previous button
    private boolean fromShutin;

    // Tracks where the done layout came from to go back
    // to said layout with the previous button
    private String toDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plate);

        // Start the Activity on the Choose Meter layout
        position = 0;

        // Add action listener to next and previous buttons
        button = findViewById(R.id.btnPrevious);
        button.setOnClickListener(PlateActivity.this);
        button.setEnabled(false);

        button = findViewById(R.id.btnNext);
        button.setOnClickListener(PlateActivity.this);


        // Set min and max value for orifice plate size
        EditText et = findViewById(R.id.etNew);
        et.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "99")});

        et = findViewById(R.id.etOriginal);
        et.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "99")});
    }

    /**
     * This is called when ever the "previous" or "next" button is pressed
     * @param view - current view
     */
    @Override
    public void onClick(View view) {
        // Check which button was pressed
        if(view.getId() == R.id.btnNext){
            next(view);
        }else if(view.getId() == R.id.btnPrevious){
            previous();
        }

    }
    /**
     * Go to the next layout when the next button was pressed
     * Hides the current layout
     * Shows the next layout
     * Updates the position index to the new layout
     */
    private void next(View view){
        switch(position){
            // On the choose meter layout
            // Go to the flow conditions layout
            case 0:
                changeLayout(R.id.layoutFlow);
                position = 1;
                button = findViewById(R.id.btnPrevious);
                button.setEnabled(true);
                break;
            // On the flow conditions layout
            // Go to maintenance layout is "Yes" was selected
            // Go to shut-in layout if "No" was selected
            case 1:
                if(getChoice(R.id.rgFlow)){
                    changeLayout(R.id.layoutMaintenance);
                    TextView tv = findViewById(R.id.txtMaintenance2);
                    tv.setText("Number 1");
                    fromShutin = false;
                    position = 3;
                }else{
                    changeLayout(R.id.layoutShutin);
                    position = 2;
                }
                break;
            // On the shut-in layout
            // Go to maintenance layout is "Yes" was selected
            // Go to end of plate replacement layout if "No" was selected
            //      and set toDone string to "shutin" to tell previous button
            //      to come back to this layout when it is pressed on the done layout
            case 2:
                if(getChoice(R.id.rgShutin)){
                    changeLayout(R.id.layoutMaintenance);
                    TextView tv = findViewById(R.id.txtMaintenance2);
                    tv.setText("Number 1");
                    fromShutin = true;
                    position = 3;
                }else{
                    changeLayout(R.id.layoutDone);
                    position = 7;
                    button = findViewById(R.id.btnNext);
                    button.setText("Finish");
                    toDone = "shutin";
                }
                break;
            // On the first maintenance layout
            // Go to check if orifice plate size is being changed layout
            case 3:
                changeLayout(R.id.layoutCheckOrifice);
                position = 4;
                break;
            // On the check if orifice plat size is changing layout
            // Go to change plate size layout is "Yes" is selected
            // Go to end of plate replacement layout if "No" is selected
            //      and set toDone string to "check" to tell previous button
            //      to come back to this layout when it is pressed on the done layout
            case 4:
                if(getChoice(R.id.rgOrifice)){
                    changeLayout(R.id.layoutChangeOrifice);
                    position = 5;
                }else{
                    changeLayout(R.id.layoutDone);
                    position = 7;
                    button = findViewById(R.id.btnNext);
                    button.setText("Finish");
                    toDone = "check";
                }
                break;
            // On the change change orifice plate size layout
            // Go to maintenance 2 layout
            case 5:
                changeLayout(R.id.layoutMaintenance);
                TextView tv = findViewById(R.id.txtMaintenance2);
                tv.setText("Number 2");
                position = 6;
                break;
            // On the maintenance 2 layout
            // Go to end of plate replacement layout
            // Change "next" button to "finish" button
            //      and set toDone string to "maintenance" to tell previous button
            //      to come back to this layout when it is pressed on the done layout
            case 6:
                changeLayout(R.id.layoutDone);
                position = 7;
                button = findViewById(R.id.btnNext);
                button.setText("Finish");
                toDone = "maintenance";
                break;
            // On the end of plate replacement layout
            // Go to the main activity when "finish" (next) button is pressed
            case 7:
                goToMainActivity(view);
            default:
                break;
        }
    }

    /**
     * Go to the previous layout when the previous button was pressed
     * Hides the current layout
     * Shows the previous layout
     * Updates the position index to the new layout
     */
    private void previous(){
        switch(position){
            // On the flow conditions layout
            // Go to the choose meter layout
            case 1:
                changeLayout(R.id.layoutMeter);
                position = 0;
                // Disable previous button b/c the Activity is now on the first layout and cannot go further back
                button = findViewById(R.id.btnPrevious);
                button.setEnabled(false);
                break;
            // On the shut-in layout
            // Go to flow conditions layout
            case 2:
                changeLayout(R.id.layoutFlow);
                position = 1;
                break;
            // On the maintenance layout
            // Goes to either the shut-in or flow layout
            // depending on which one it came from
            case 3:
                if(fromShutin){
                    changeLayout(R.id.layoutShutin);
                    position = 2;
                }else{
                    changeLayout(R.id.layoutFlow);
                    position = 1;
                }
                break;
            // On the check change orifice plate size layout
            // Go to first maintenance layout
            case 4:
                changeLayout(R.id.layoutMaintenance);
                position = 3;
                break;
            // On the change orifice plate size layout
            // Go to check if orifice plate size is changing layout
            case 5:
                changeLayout(R.id.layoutCheckOrifice);
                position = 4;
                break;
            // On the second maintenance layout
            // Go to change orifice plate size layout
            case 6:
                changeLayout(R.id.layoutChangeOrifice);
                position = 5;
                break;
            // On the done layout
            // Check which layout the done layout came from and go back to that one
            case 7:
                button = findViewById(R.id.btnNext);
                button.setText("Next");
                switch(toDone){
                    case "shutin":
                        changeLayout(R.id.layoutShutin);
                        position = 2;
                        break;
                    case "check":
                        changeLayout(R.id.layoutCheckOrifice);
                        position = 4;
                        break;
                    default:
                        changeLayout(R.id.layoutMaintenance);
                        position = 6;
                        break;
                }
               break;
            default:
                break;
        }
    }

    /**
     * Hides the current layout and displays the new one on the Activity
     * @param layout - int ID of the new layout to show
     */
    private void changeLayout(int layout){
        // Hide all of the layouts
        cl = findViewById(R.id.layoutFlow);
        cl.setVisibility(ConstraintLayout.INVISIBLE);
        cl = findViewById(R.id.layoutShutin);
        cl.setVisibility(ConstraintLayout.INVISIBLE);
        cl = findViewById(R.id.layoutMeter);
        cl.setVisibility(ConstraintLayout.INVISIBLE);
        cl = findViewById(R.id.layoutChangeOrifice);
        cl.setVisibility(ConstraintLayout.INVISIBLE);
        cl = findViewById(R.id.layoutCheckOrifice);
        cl.setVisibility(ConstraintLayout.INVISIBLE);
        cl = findViewById(R.id.layoutMaintenance);
        cl.setVisibility(ConstraintLayout.INVISIBLE);
        cl = findViewById(R.id.layoutDone);
        cl.setVisibility(ConstraintLayout.INVISIBLE);
        // Display the new layout
        cl = findViewById(layout);
        cl.setVisibility(ConstraintLayout.VISIBLE);
    }

    /**
     * Get the selected value in a Yes/No Radio Group
     * @param id - int ID of the radio group
     * @return - boolean
     *                  True:  for yes
     *                  False: for no or any other option
     */
    private boolean getChoice(int id){
        RadioGroup rg = findViewById(id);
        RadioButton rb = findViewById(rg.getCheckedRadioButtonId());
        return rb.getText().equals("Yes");
    }


    /**
     * Add to a button to go back to the main menu
     * Clears all active activities from the screen and refreshes
     * @param view - current view
     */
    public void goToMainActivity(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
