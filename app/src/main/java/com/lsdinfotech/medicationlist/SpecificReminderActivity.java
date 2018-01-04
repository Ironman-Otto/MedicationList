package com.lsdinfotech.medicationlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import data.Constants;
import model.MyEvent;
import utility.EventReminder;
import utility.StringUtil;
import utility.TimeDatePickers;

public class SpecificReminderActivity extends AppCompatActivity {

    private EditText                reminderName;
    private TextView                time;
    private TextView                date;
    private ImageButton             timeBtn;
    private ImageButton             dateBtn;
    private TimeDatePickers         timeDatePickers;
    private static final String     DATE_FORMAT = "MM-dd-yyyy";
    private static final String     TIME_FORMAT = "hh:mm aa";
    private static final String     DATE_TIME_FORMAT = DATE_FORMAT + " " + TIME_FORMAT;
    private String                  timeValue;
    private String                  dateValue;
    private String                  reminderNameValue;
    private int                     mode;
    private String                  eventGson;
    private EventReminder           eventReminder;
    private MyEvent                 incomingEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_reminder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        timeDatePickers = new TimeDatePickers(SpecificReminderActivity.this, DATE_FORMAT, TIME_FORMAT);

        SharedPreferences shdpref = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        long calendarID = shdpref.getLong(getString(R.string.saved_calendar_id), 0);
        String accountName = shdpref.getString(getString(R.string.saved_account_name), "");
        if (calendarID != 0 && StringUtil.isNotNullEmptyBlank(accountName)) {
            eventReminder = new EventReminder(getApplicationContext(), calendarID, accountName);
        }

        mode = getIntent().getIntExtra(Constants.MODE, -1);
        eventGson = getIntent().getStringExtra(Constants.EVENT);
        setUpViews();
        setUpEventHandlers();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if ( fab != null ) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    readScreenInput();
                    if ( verifyInput() ){
                        setReminder();
                        finish();
                    } else {
                        Snackbar.make(view, "Complete all entries", Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mode == Constants.UPDATE) {
            fillScreen();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_specific_reminder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_delete:{
                if(mode == Constants.UPDATE && incomingEvent != null) {
                    eventReminder.deleteEvent(getApplicationContext(), incomingEvent.getEventId());
                    finish();
                }
                break;
            }
        }
        return true;
    }

    /**
     * setUpViews
     * This method sets up all the view for the activity
     */
    private void setUpViews() {
        reminderName    = (EditText) findViewById(R.id.specific_reminder_name);
        time            = (TextView) findViewById(R.id.specific_reminder_time);
        timeBtn         = (ImageButton) findViewById(R.id.specific_reminder_time_btn);
        date            = (TextView) findViewById(R.id.specific_reminder_date);
        dateBtn         = (ImageButton) findViewById(R.id.specific_reminder_date_btn);
    }

    /**
     * setUpEventHandlers
     * this method sets up the event handlers for the time and date
     * inputs
     */
    private void setUpEventHandlers() {
        timeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeDatePickers.showTimePicker(time);
            }
        });

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeDatePickers.showDatePicker(date);
            }
        });
    }

    /**
     * fillScreen
     * This method gets the event that was passed in during an update
     * and sets the reminder name, time, and date on the screen
     */
    private void fillScreen() {
        Gson gson       = new Gson();
        incomingEvent   = gson.fromJson(eventGson, MyEvent.class);
        reminderName.setText(incomingEvent.getEventTitle());

        DateFormat datedf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        date.setText(datedf.format(incomingEvent.getDtstart()));

        DateFormat timedf = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
        time.setText(timedf.format(incomingEvent.getDtstart()));
    }

    /**
     * readScreenInput
     * This method gets the current text values of the
     * UI views. Trim the reminder name for uniformity.
     */
    private void readScreenInput() {
        timeValue           = time.getText().toString();
        dateValue           = date.getText().toString();
        reminderNameValue   = reminderName.getText().toString().trim();
    }

    /**
     * verifyInput
     * This method verifies that all required fields are set.
     * It returns true if all are valid, false otherwise.
     * @return boolean result of test
     */
    private boolean verifyInput() {
        return (StringUtil.isNotNullEmptyBlank(timeValue) &&
                StringUtil.isNotNullEmptyBlank(dateValue) &&
                StringUtil.isNotNullEmptyBlank(reminderNameValue));
    }

    /**
     * setReminder
     * This  method sets the event and reminder for a specific date and time
     * if it is a new event the new event is inserted by non recurring method
     * and a reminder is set for the new event. If on the other hand it is an
     * update event the mode is set to update and the current event id is passed
     * to the non recurring reminder method. In the case of updates the reminder is left
     * as it was originally.
     */
    private void setReminder() {
        long dtStart;
        dtStart = setDateTime();
        long eventID = 0;

        if (eventReminder != null) {
            if (mode == Constants.UPDATE) {
                eventReminder.setMode(Constants.UPDATE);
                eventReminder.setEventId(incomingEvent.getEventId());
            }

            if (dtStart != 0 && StringUtil.isNotNullEmptyBlank(reminderNameValue)) {
                eventID = eventReminder.addEventNonRecurring(reminderNameValue, "", dtStart, dtStart,
                        eventReminder.getDefaultTimeZoneID());
            }

            if (eventID != 0) {
                if(mode != Constants.UPDATE){
                    eventReminder.addReminder(eventID, 0, CalendarContract.Reminders.METHOD_ALERT);
                }
            }
        }


    }

    /**
     * setDateTime
     * This method creates a string containing the date and time from the UI.
     * The string is then parsed into a Date object. The date is then
     * converted to milliseconds.
     * @return long with date in milleseconds or 0 if there is a parse exception
     */
    private long setDateTime() {
        DateFormat dfdate = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault());
        String dtStart = dateValue + " " + timeValue;
        try {
            Date dt = dfdate.parse(dtStart);
            return dt.getTime();
        } catch (ParseException pe){
            return 0;
        }

    }
}
