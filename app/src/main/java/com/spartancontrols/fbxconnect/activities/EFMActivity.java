package com.spartancontrols.fbxconnect.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.spartancontrols.fbxconnect.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class EFMActivity extends AppCompatActivity
        implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private Button button;
    private Integer id;

    // Stores what the user has selected for date and time to grab data from
    private static Calendar dateFrom;
    private static Calendar dateTo;

    // Tracks which button was pressed for the picker fragments to store the
    // data back into one of the Calendars above
    private static boolean from;

    // Stores the current selected station:
    //      stationALL, station1, station2
    private String selectedStation;

    // Tracks if user has selected one of the options on the page
    //          True: the item is selected
    //          False: the item is not selected
    private boolean daily;
    private boolean hourly;
    private boolean alarms;
    private boolean events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efm);

        // Generate new default calendars and set them to the current date and time
        dateTo = new GregorianCalendar();
        dateFrom = new GregorianCalendar();

        // Displays the Calendar data and times to the appropriate buttons on the screen
        setDefaultDateTime();

        from = true;

        // Default toggle for all history types is off
        daily = false;
        hourly = false;
        alarms = false;
        events = false;

        // Set default selected station
        selectedStation = "stationAll";

        // Disable Meter 1 ad n2 buttons to toggle until the correct Station has been selected
        RadioButton rb = findViewById(R.id.rbMeter1);
        rb.setEnabled(false);
        rb = findViewById(R.id.rbMeter2);
        rb.setEnabled(false);

        // Add listener to Select a Station radiogroup
        RadioGroup radioStation = findViewById(R.id.rgStation);
        radioStation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedID) {
                stationSelection(checkedID);
            }
        });

        // Add listener to toggle boolean when any of the history checkboxes are pressed
        CheckBox checkBox = findViewById(R.id.cbDaily);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dailyToggle(isChecked);
            }
        });

        checkBox = findViewById(R.id.cbHourly);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hourlyToggle(isChecked);
            }
        });

        checkBox = findViewById(R.id.cbAlarms);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                alarmsToggle(isChecked);
            }
        });

        checkBox = findViewById(R.id.cbEvents);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                eventsToggle(isChecked);
            }
        });
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

    /**
     * Add to a button to go to the Report Activity
     * Grabs all user inputs and passed them to the Report Activity
     * @param view - current view
     */
    public void goToReportAction(View view) {
        Calendar cal = new GregorianCalendar();
        // Checks if at least one of Daily or Hourly has been selected
        // Need one of theses to send a request for data from the FB1100/1200
        if (daily || hourly) {
            if (compareCalendar(cal, dateTo) || compareCalendar(cal, dateFrom)) {
                String text = "Please ensure that the Dates and Times are not set to be in the future";
                displayToast(text, Toast.LENGTH_LONG);
                displayToast(text, Toast.LENGTH_LONG);
                // If the From calendar is less than the To calendar go to the report screen
            } else if (compareCalendar(dateFrom, dateTo)) {
                Intent intent = new Intent(this, ReportActivity.class);
                intent.putExtra("from", dateFrom.getTimeInMillis());
                intent.putExtra("to", dateTo.getTimeInMillis());
                intent.putExtra("station", selectedStation);
                intent.putExtra("daily", daily);
                intent.putExtra("hourly", hourly);
                intent.putExtra("alarms", alarms);
                intent.putExtra("events", events);
                intent.putExtra("report", "efm");
                startActivity(intent);
                // Else, display error message to user and stay on current screen.
            } else {
                String text = "Please ensure the \"From Date and Time\" is set to before the \"To Date and Time\"";
                displayToast(text, Toast.LENGTH_LONG);
                displayToast(text, Toast.LENGTH_LONG);
            }
        // If neither is selected send a
        } else {
            String text = "Please select at least one of Daily or Hourly";
            displayToast(text, Toast.LENGTH_LONG);
            // If either of the calendars are greater than the current date and time
        }

    }


    /**
     * Add to button to display a time picker
     *
     * @param view - current view
     */
    public void showTimePickerDialog(View view) {
        id = view.getId();
        button = findViewById(id);
        from = button.getContentDescription().toString().equals("timeFrom");
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    /**
     * Add to button to display a date picker
     *
     * @param view - current view
     */
    public void showDatePickerDialog(View view) {
        id = view.getId();
        button = findViewById(id);
        from = button.getContentDescription().toString().equals("dateFrom");
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * Set the time for either From or To depending on what button was hit
     * Will take the TimePickerFragment time and display it onto the button that was pressed
     * And store it in the correct Calendar variable
     * @param view      - current view
     * @param hourOfDay - int
     * @param minute    - int
     */
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        // Set the time for either from or to
        if (button.getContentDescription().toString().equals("timeFrom")) {
            dateFrom.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateFrom.set(Calendar.MINUTE, minute);
            button.setText(time(dateFrom));
        } else {
            dateTo.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateTo.set(Calendar.MINUTE, minute);
            button.setText(time(dateTo));
        }

    }

    /**
     * Set the date for either From or To depending on what button was hit
     * Will take the DatePickerFragment date and display it onto the button that was pressed
     * And store it in the correct Calendar variable
     * @param view - current view
     * @param year - int to set to the Calendar
     * @param month - int to set to the Calendar
     * @param day - int to set to the Calendar
     */
    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user

        if (button.getContentDescription().toString().equals("dateFrom")) {
            dateFrom.set(year, month, day);
            button.setText(date(dateFrom));
        } else {
            dateTo.set(year, month, day);
            button.setText(date(dateTo));
        }
    }

    /**
     * Class used to get and display the time picked in the frame picker
     */
    public static class TimePickerFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c;
            // If the button pressed was the FromTime Button
            // Take the dateFrom Calendar as the starting value for the TimePickerFragment
            if(from)
                c = dateFrom;
            else
                c = dateTo;
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), AlertDialog.THEME_HOLO_DARK, (TimePickerDialog.OnTimeSetListener) getActivity(), hour, minute, DateFormat.is24HourFormat(getActivity()));
        }
    }

    public static class DatePickerFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker

            final Calendar c;
            // If the button pressed was the FromDate Button
            // Take the dateFrom Calendar as the starting value for the DatePickerFragment
            if(from)
                c = dateFrom;
            else
                c = dateTo;

            // Variables to set to the DatePickerFragment from the Calendar
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_DARK, (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
            DatePicker datePicker = dialog.getDatePicker();
            Calendar cal = Calendar.getInstance();
            datePicker.setMaxDate(cal.getTimeInMillis());
            return dialog;
        }
    }

    /**
     * Sets the date and time of the buttons to the date and time
     * to when the Activity is opened
     */
    private void setDefaultDateTime() {
        button = findViewById(R.id.btnFromDate);
        button.setText(date(dateFrom));

        button = findViewById(R.id.btnToDate);
        button.setText(date(dateTo));

        button = findViewById(R.id.btnFromTime);
        button.setText(time(dateFrom));

        button = findViewById(R.id.btnToTime);
        button.setText(time(dateTo));
    }

    /**
     * Changes the selected station and toggles the possible meters that can be pressed
     *
     * @param checkedID - int ID of selected station in radioGroup
     */
    private void stationSelection(int checkedID) {
        RadioButton radioButton;
        // When All stations is selected
        // Disable meters 1 and 2 and toggle All meters on
        switch (checkedID) {
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

    /**
     * Toggles the boolean for the following 4 functions when the check box is pressed
     *
     * @param isChecked - toggle boolean to this value
     */
    private void dailyToggle(boolean isChecked) {
        daily = isChecked;
    }

    private void hourlyToggle(boolean isChecked) {
        hourly = isChecked;
    }

    private void eventsToggle(boolean isChecked) {
        events = isChecked;
    }

    private void alarmsToggle(boolean isChecked) {
        alarms = isChecked;
    }

    /**
     * Compare to calendar objects
     *
     * @param from - first Calendar
     * @param to   - second Calendar
     * @return - boolean
     * true: if first is less than second
     * false: if first is equal to or greater than second
     */
    private boolean compareCalendar(Calendar from, Calendar to) {
        if (from == null || to == null)
            return false;

        int comp = from.compareTo(to);
        return comp < 0;
    }

    /**
     * Display a message to the user at the bottom of the screen
     *
     * @param text     - Message to display
     * @param duration - time on screen
     *                  E.g. Toast.LENGTH_LONG, Toast.LENGTH_SHORT
     */
    private void displayToast(String text, Integer duration) {
        Context context = getApplicationContext();

        Toast.makeText(context, text, duration).show();
    }

    /**
     * Format the Calendar time into user readable 12 hour format
     *
     * @param cal - Calendar to get the time from
     * @return - String format: hour:minute AM/PM
     *          E.g: 4:05 PM, 10:45 AM
     */
    private String time(Calendar cal) {
        int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        String time = "AM";
        String min = Integer.toString(minute);
        if (hourOfDay > 12) {
            hourOfDay = hourOfDay - 12;
            time = "PM";
        }
        if (minute < 10)
            min = "0" + min;
        // Set the text of the button that was pressed to time selected
        return hourOfDay + ":" + min + " " + time;
    }

    /**
     * Format the Calendar date into user readable day/month/year format
     * @param cal - Calendar to grab date from
     * @return - String format: day/month/year
     *             E.g: 25/1/2008,  9/4/2018
     */
    private String date(Calendar cal){
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return day + "/" + (month+1) + "/" + year;
    }
}
