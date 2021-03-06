package com.spartancontrols.fbxconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.automatak.dnp3.DNP3Manager;
import com.automatak.dnp3.Master;
import com.opencsv.CSVWriter;
import com.spartancontrols.fbxconnect.Globals;
import com.spartancontrols.fbxconnect.R;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class ReportActivity extends AppCompatActivity {

    static {
        System.loadLibrary("opendnp3java");
    }

    // Store all of the params passed by the user from the previous screen
    private Calendar dateFrom;
    private Calendar dateTo;
    private String station;
    private boolean daily;
    private boolean hourly;
    private boolean alarms;
    private boolean events;

    private DNP3Manager manager;
    private Master master;


    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Get the DNP3Manager and Master from the Global variables class
        Globals g = (Globals)getApplication();
        manager = g.getDNP3Manager();
        master = g.getMaster();

        intent = getIntent();
        readIntent();
    }

    /**
     * Chooses the correct layout depending on what Activity the user came from
     * Either the Gas Composition or EFM report Activity
     * Displays the necessary data the user passed to the screen
     */
    private void readIntent() {
        try {
            String report = intent.getStringExtra("report");
            switch (report) {
                case "gas": {
                    ConstraintLayout cl = this.findViewById(R.id.layoutGas);
                    cl.setVisibility(ConstraintLayout.VISIBLE);
                    setGasReport(intent);
                    break;
                }
                case "efm": {
                    ConstraintLayout cl = this.findViewById(R.id.layoutEFM);
                    cl.setVisibility(ConstraintLayout.VISIBLE);
                    setEFMReport(intent);
                    break;
                }
                // Used if the user somehow got to this Activity from neither of the above ones
                default:
                    TextView txt = findViewById(R.id.txtReport);
                    String temp = "Error: This page should only be visible by generating a report from either EFM or Gas Composition";
                    txt.setText(temp);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param name - file name to be generated
     * @param type - folder to be placed in (Gas or EFM)
     * @param data - string of all of the data to be stored in the file
     */
    private void generateCSV(String name, String type, String data) {
        try {
            // Find the directory for Documents on the phone
            File directory = new File(Environment.getExternalStorageDirectory().getPath() + "/Documents/" + type);
            // If the directory was found
            if (directory.exists()) {
                System.out.println("CSV File location all ready created: " + directory.getAbsolutePath());
            } else {
                // If not found try to make the Documents directory
                directory.mkdir();
                System.out.println("CSV File location being created: " + directory.getAbsolutePath());
            }
            // Create a new File with a .csv extension
            File file = new File(directory, name + ".csv");
            // Create a new CSV file from the file that was created
            CSVWriter writer = new CSVWriter(new FileWriter(file), ',', ' ');
            // Create an array of the data that was passed
            String[] entries = data.split("#"); // array of your values
            // Write all of the data to the new file
            writer.writeNext(entries);
            // CLose out of the file
            writer.close();
        } catch (IOException e) {
            System.out.println("CSV ERROR: " + e.getMessage());
            System.out.println("CSV Attempting to save in Downloads folder");
            // If the function could not save to the Documents folder, tries to do the same in the downloads
            try {
                File directory = new File(Environment.getExternalStorageDirectory().getPath() + "/Download/" + type);
                if (directory.exists()) {
                    System.out.println("CSV File location all ready created: " + directory.getAbsolutePath());
                } else {
                    directory.mkdir();
                    System.out.println("CSV File location being created: " + directory.getAbsolutePath());
                }
                File test = new File(directory, name + ".csv");
                CSVWriter writer = new CSVWriter(new FileWriter(test), ',', ' ');
                String[] entries = data.split("#"); // array of your values
                writer.writeNext(entries);
                writer.close();
            } catch (IOException r) {
                System.out.println("CSV ERROR: " + r.getMessage());
                System.out.println("CSV could not save to Downloads or Documents folder");
            }
        }
    }

    /**
     * Add to a button to go back to the main menu
     * Clears all active activities from the screen and refreshes
     *
     * @param view - current view
     */
    public void goToMainActivity(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }

    /**
     * Set the report activity to the EFM layout
     * Display all the settings user picked on EFM page
     *
     * @param intent - params passed from previous activity
     */
    private void setEFMReport(Intent intent) {
        TextView txt = findViewById(R.id.txtReport);
        txt.setText("View EFM Report");

        station = intent.getStringExtra("station");
        txt = findViewById(R.id.txtStationEFM);
        txt.setText(station);

        daily = intent.getBooleanExtra("daily", false);
        txt = findViewById(R.id.txtDaily);
        txt.setText("Daily: " + daily);

        hourly = intent.getBooleanExtra("hourly", false);
        txt = findViewById(R.id.txtHourly);
        txt.setText("Hourly: " + hourly);

        events = intent.getBooleanExtra("events", false);
        txt = findViewById(R.id.txtEvents);
        txt.setText("Events: " + events);

        alarms = intent.getBooleanExtra("alarms", false);
        txt = findViewById(R.id.txtAlarms);
        txt.setText("Alarms: " + alarms);

        Long date = intent.getLongExtra("from", 0);
        dateFrom = new GregorianCalendar();
        dateFrom.setTimeInMillis(date);
        txt = findViewById(R.id.txtFrom);
        txt.setText(dateFrom.getTime().toString());

        date = intent.getLongExtra("to", 0);
        dateTo = new GregorianCalendar();
        dateTo.setTimeInMillis(date);
        txt = findViewById(R.id.txtTo);
        txt.setText(dateTo.getTime().toString());

        String time = date(dateFrom) + " " + date(dateTo);
        String data = "Station:" + station + "#Daily:" + daily + "#Hourly:" + hourly + "#Events:" + events + "#Alarms:" + alarms + "#From:" + date(dateFrom) + "#To:" + date(dateTo);
        generateCSV(time, "EFM", data);
    }

    /**
     * Set the report activity to the Gas Composition Report layout
     * Display the station the user selected on the previous page
     *
     * @param intent - params passed from previous activity
     */
    private void setGasReport(Intent intent) {
        TextView txt = findViewById(R.id.txtReport);
        txt.setText("View Gas Composition Report");

        txt = findViewById(R.id.txtStationGas);
        txt.setText(intent.getStringExtra("station"));

        String date = date(Calendar.getInstance());
        String data = intent.getStringExtra("station");
        generateCSV(date, "Gas", data);
    }

    /**
     * Format the Calendar date for output csv file
     *
     * @param cal - Calendar to grab date from
     * @return - String format: year-month-day--hour-minute
     * E.g: 2008-01-18--09-53,  1996-11-09--23-45
     */
    private String date(Calendar cal) {
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month += 1;
        String mon = Integer.toString(month);
        if (month < 10) {
            mon = "0" + mon;
        }
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String d = Integer.toString(day);
        if (day < 10) {
            d = "0" + day;
        }
        return year + "-" + mon + "-" + d + "--" + time(cal);
    }

    private String time(Calendar cal) {
        int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        String min = Integer.toString(minute);
        String h = Integer.toString(hourOfDay);
        if (hourOfDay < 10) {
            h = "0" + h;
        }
        if (minute < 10)
            min = "0" + min;
        // Set the text of the button that was pressed to time selected
        return h + "-" + min;
    }
}

