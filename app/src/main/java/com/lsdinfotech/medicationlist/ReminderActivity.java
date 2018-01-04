package com.lsdinfotech.medicationlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import data.Constants;
import model.MyEvent;
import utility.EventReminder;
import utility.ReminderInputDialogs;
import utility.SpinnerConstants;
import utility.StringUtil;
import utility.TimeDatePickers;

public class ReminderActivity extends AppCompatActivity {
    private EditText            reminderName;
    private TextView            daysToOccur;
    private ImageButton         daysToOccurBtn;
    private TextView            daysToOccurLabel;
    private TextView            time;
    private ImageButton         timeBtn;
    private TextView            occurrence;
    private TextView            occurrenceInterval;
    private ImageButton         occurrenceIntervalBtn;
    private TextView            occurrenceCount;
    private ImageButton         occurrenceCountBtn;
    private TextView            date;
    private ImageButton         dateBtn;
    private TextView            until;
    private ImageButton         untilBtn;

    private TimeDatePickers     timeDatePickers;
    private static final String DATE_FORMAT = "MM-dd-yyyy";
    private static final String TIME_FORMAT = "hh:mm aa";
    private EventReminder       eventReminder;
    private MyEvent             incomingEvent;
    private String              occurrenceValue;
    private int                 interval;
    private int                 mode;
    private String              eventGson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        processIntent();
        setUpViews();
        setUpEventHandlers();

        eventReminder = createEventReminder();
        incomingEvent = new MyEvent();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mode == Constants.UPDATE){
            fillScreen();
        } else {
            occurrence.setText(occurrenceValue);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reminder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_delete:{
                if(mode == Constants.UPDATE && incomingEvent != null){
                    eventReminder.deleteEvent(getApplicationContext(), incomingEvent.getEventId());
                    finish();
                }
                break;
            }
            case R.id.action_save:{
                processSaveAction();
                break;
            }
        }
        return true;
    }

    /**
     * processSaveAction
     * This method will read screen input and save reminder
     */
    private void processSaveAction() {
        View view = this.getWindow().findViewById(android.R.id.content);
        readScreenInput();
        if (verifyScreenInput()) {
            setReminder();
            finish();
        } else {
            Snackbar.make(view, R.string.reminder_activity_verify_msg, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * processIntent
     * This method checks the incoming intent and sets the
     * appropriate values.
     */
    private void processIntent() {
        occurrenceValue = getIntent().getStringExtra(Constants.FREQ);
        interval = getIntent().getIntExtra(Constants.INTERVAL, -1);
        mode = getIntent().getIntExtra(Constants.MODE, -1);
        eventGson = getIntent().getStringExtra(Constants.EVENT);

    }

    /**
     * createEventReminder
     * This method reads the shared preferences holding calendar specifics.
     * It then creates an EventReminder object
     * @return EventReminder object or null
     */
    private EventReminder createEventReminder() {
        SharedPreferences shdpref = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        long calendarID = shdpref.getLong(getString(R.string.saved_calendar_id), 0);
        String accountName = shdpref.getString(getString(R.string.saved_account_name), "");
        if (calendarID != 0 && StringUtil.isNotNullEmptyBlank(accountName)) {
            return new EventReminder(getApplicationContext(), calendarID, accountName);
        }

        return null;
    }
    /**
     * fillScreen
     * This method gets the event that was sent to the ReminderActivity during
     * an update. It reads the json string containing event and places it in event
     * object. The RRULE in the event is parsed into its components and used
     * to set the fields of the screen. Note the count could be null. If it is
     * then the default value of indefinite is set as the value.
     */
    private void fillScreen() {
        Gson gson = new Gson();
        incomingEvent = gson.fromJson(eventGson, MyEvent.class);
        incomingEvent.parseRrule();
        incomingEvent.expandByDay();

        reminderName.setText(incomingEvent.getEventTitle());
        DateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
        time.setText(dateFormat.format(incomingEvent.getDtstart()));
        DateFormat dateFormat1 = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        date.setText(dateFormat1.format(incomingEvent.getDtstart()));
        occurrence.setText(incomingEvent.getFreq());
        daysToOccur.setText(incomingEvent.getByDayExpanded());

        if(StringUtil.isNotNullEmptyBlank(incomingEvent.getInterval())) {
            int offset = Integer.valueOf(incomingEvent.getInterval()) - 1;
            occurrenceInterval.setText(setInterval(incomingEvent.getFreq(), offset));
        }

        if (StringUtil.isNotNullEmptyBlank(incomingEvent.getCount())) {
            int offset = Integer.valueOf(incomingEvent.getCount());
            occurrenceCount.setText(SpinnerConstants.spinnerCount[offset]);
        }

        if (StringUtil.isNotNullEmptyBlank(incomingEvent.getUntil())) {
            until.setText(incomingEvent.formatUntilDate());
        }
    }

    /**
     * setInterval
     * This method selects the default interval based on the current
     * value of the occurrence frequency
     * @param freq frequency of occurrence (DAILY, WEEKLY, ...)
     * @return the default interval
     */
    private String setInterval(String freq, int offset) {

        String interval;
        switch (freq.toUpperCase()) {
            case "HOURLY": {
                interval = SpinnerConstants.spinnerHours[offset];
                break;
            }
            case "DAILY": {
                interval = SpinnerConstants.spinnerDays[offset];
                break;
            }
            case "WEEKLY": {
                interval = SpinnerConstants.spinnerWeeks[offset];
                break;
            }
            case "MONTHLY": {
                interval = SpinnerConstants.spinnerMonths[offset];
                break;
            }
            case "YEARLY": {
                interval = SpinnerConstants.spinnerYears[offset];
                break;
            }
            default:
                interval = null;
        }

        return interval;
    }
    /**
     * setUpViews
     * This method sets all the view for the activity
     */
    private void setUpViews() {
        reminderName            = (EditText) findViewById(R.id.reminder_name);
        daysToOccur             = (TextView) findViewById(R.id.reminder_days_to_occur);
        daysToOccurBtn          = (ImageButton) findViewById(R.id.reminder_days_to_occur_btn);
        daysToOccurLabel        = (TextView) findViewById(R.id.reminder_days_to_occur_label);
        time                    = (TextView) findViewById(R.id.reminder_time);
        timeBtn                 = (ImageButton) findViewById(R.id.reminder_time_btn);
        occurrence              = (TextView) findViewById(R.id.reminder_occurrence);
        occurrenceInterval      = (TextView) findViewById(R.id.reminder_occurrence_interval);
        occurrenceIntervalBtn   = (ImageButton) findViewById(R.id.reminder_occurrence_interval_btn);
        occurrenceCount         = (TextView) findViewById(R.id.reminder_occurrence_count);
        occurrenceCountBtn      = (ImageButton) findViewById(R.id.reminder_occurrence_count_btn);
        date                    = (TextView) findViewById(R.id.reminder_start_date);
        dateBtn                 =(ImageButton) findViewById(R.id.reminder_start_date_btn);
        until                   = (TextView) findViewById(R.id.reminder_until);
        untilBtn                = (ImageButton) findViewById(R.id.reminder_until_btn);
    }

    /**
     * setUpEventHandlers
     * This method sets the event handlers for all the required actions
     */
    private void setUpEventHandlers() {
        timeDatePickers = new TimeDatePickers(ReminderActivity.this, DATE_FORMAT, TIME_FORMAT);

        if(!occurrenceValue.equals("DAILY") && !occurrenceValue.equals("YEARLY")) {
            daysToOccurBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ReminderInputDialogs(ReminderActivity.this, daysToOccur).getDaysOfWeek().show();
                }
            });
        } else {
            daysToOccurLabel.setText(R.string.days_to_occur_label_na);
        }

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

        occurrenceIntervalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ReminderInputDialogs(ReminderActivity.this, getApplicationContext(), occurrenceInterval, null, interval)
                        .getOccurrenceInterval().show();
            }
        });

        occurrenceCountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ReminderInputDialogs(ReminderActivity.this, getApplicationContext(), null, occurrenceCount, 0)
                        .getOccurrenceCount().show();
            }
        });

        untilBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ReminderInputDialogs(ReminderActivity.this, until).getUntil().show();
            }
        });
    }

    /**
     * readScreenInput
     * This method gets the days of occurance, the time, reminder name
     * and all other values needed to calculate the recursion rule
     */
    private void readScreenInput() {

        incomingEvent.setEventTitle(reminderName.getText().toString());
        incomingEvent.setTime(time.getText().toString());
        incomingEvent.setDate(date.getText().toString());
        incomingEvent.setFreq(occurrence.getText().toString());
        incomingEvent.setInterval(occurrenceInterval.getText().toString());
        incomingEvent.setCount(occurrenceCount.getText().toString());
        incomingEvent.setByDay(daysToOccur.getText().toString());
        incomingEvent.setUntil(until.getText().toString());

    }

    /**
     * verifyScreenInput
     * This method verifies the input from the screen is valid
     * @return true if all values present, false if something is missing
     */
    private boolean verifyScreenInput() {
          return StringUtil.isNotNullEmptyBlank(incomingEvent.getTime()) &&
                StringUtil.isNotNullEmptyBlank(incomingEvent.getDate()) &&
                StringUtil.isNotNullEmptyBlank(incomingEvent.getEventTitle());
    }

    /**
     * setReminder
     * This method collects all necessay information and
     * enters an event in the current calendar. The event
     * can be a new event or update of an existing event.
     * New events are assigned a reminder. During updates the reminder
     * remains the same. Reminders follow the event specifics. It is not
     * necessary to update the reminder in this application
     */
    private void setReminder() {
        long eventID;
        long startMillis = incomingEvent.getMillis();
        String rRule = incomingEvent.generateRRule();

        if (eventReminder != null && startMillis != 0 && StringUtil.isNotNullEmptyBlank(rRule)) {
            if (mode == Constants.UPDATE) {
                eventReminder.setMode(Constants.UPDATE);
                eventReminder.setEventId(incomingEvent.getEventId());
            }

            eventID = eventReminder.addEventRecurring(incomingEvent.getEventTitle(),"",startMillis,rRule,eventReminder.getDefaultTimeZoneID());

            if (eventID != 0 && mode != Constants.UPDATE) {
                eventReminder.addReminder(eventID, 0, CalendarContract.Reminders.METHOD_ALERT);

            }
        }
    }

}
