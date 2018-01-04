package com.lsdinfotech.medicationlist;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

import data.Constants;
import data.ReminderListArrayAdapter;
import model.MyEvent;
import utility.EventReminder;
import utility.MyCalendar;
import utility.ReminderInputDialogs;
import utility.SpinnerConstants;
import utility.StringUtil;

public class ReminderListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private ListView                reminderListView;
    private ArrayList<MyEvent>      reminderArrayList;
    private ArrayAdapter<MyEvent>   arrayAdapter;
    private EventReminder           eventReminder;
    private static final int        REQUEST_CALENDER_PERMISSIONS = 1000;
    private FloatingActionButton    fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(checkCalendarPermissions()) {
            setUpActivity();
        } else {
            ActivityCompat.requestPermissions(ReminderListActivity.this,
                    new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR},
                    REQUEST_CALENDER_PERMISSIONS);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(checkCalendarPermissions()) {
            new ReminderAsyncTask().execute("");
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        if (!reminderArrayList.get(position).isRecurring()){
            intent.setClass(getApplicationContext(), SpecificReminderActivity.class);
        } else {
            intent.setClass(getApplicationContext(), ReminderActivity.class);
            intent.putExtra(Constants.FREQ, reminderArrayList.get(position).getFreq());
            intent.putExtra(Constants.INTERVAL, mapFreqToInt(reminderArrayList.get(position).getFreq()));
        }

        intent.putExtra(Constants.MODE, Constants.UPDATE);
        Gson gson = new Gson();
        intent.putExtra(Constants.EVENT, gson.toJson(reminderArrayList.get(position)));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CALENDER_PERMISSIONS:{
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    setUpActivity();
                }
            }
        }

    }

    private void setUpActivity() {
        createCalendar();
        setUpViews();
        setUpEventHandlers();
        setUpArrayAdapter();
        setEventReminder();
    }

    /**
     * checkCalendarPermissions
     * Check if we have read and write calendar permissions
     * @return true if we have both.
     */
    private boolean checkCalendarPermissions() {

        int readCalPermission = ContextCompat.checkSelfPermission(ReminderListActivity.this,
                android.Manifest.permission.READ_CALENDAR);
        int writeCalPermission = ContextCompat.checkSelfPermission(ReminderListActivity.this,
                android.Manifest.permission.WRITE_CALENDAR);

        return (readCalPermission == PackageManager.PERMISSION_GRANTED) &&
                (writeCalPermission == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * createCalendar
     * This method creates a calendar for the application if it does not already
     * exist. The calendar properties are then saved in shared preferrences.
     */
    private void createCalendar() {
        long savedCalendarId;
        MyCalendar calendar = new MyCalendar();

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.saved_account_name), getString(R.string.cal_account_name));
        editor.putString(getString(R.string.saved_account_owner), getString(R.string.cal_account_owner));
        editor.putString(getString(R.string.saved_display_name), getString(R.string.cal_display_name));
        savedCalendarId = sharedPref.getLong(getString(R.string.saved_calendar_id), 0);

        MyCalendar.InstalledCalendar medCal = calendar.getCalendar(getApplicationContext(), getString(R.string.cal_account_name));
        if (medCal == null) {
            long calId = calendar.createCalendar(getApplicationContext(), getString(R.string.cal_account_name),
                    getString(R.string.cal_display_name), getString(R.string.cal_account_owner), 1);
            editor.putLong(getString(R.string.saved_calendar_id), calId);
        } else if (savedCalendarId == 0) {
            editor.putLong(getString(R.string.saved_calendar_id), medCal.getCalId());
        }

        editor.apply();
    }

    private void setUpViews() {
        reminderListView = (ListView) findViewById(R.id.reminder_listview);
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    private void setUpEventHandlers() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ReminderInputDialogs(ReminderListActivity.this).selectReminderType().show();

            }
        });

        reminderListView.setOnItemClickListener(this);
    }

    /**
     * mapFreqToInt
     * This method maps the current freq (occurrence) into the
     * interval number that selects the proper spinner for
     * the reminder interval spinner
     * @param freq occurrence DAILY...
     * @return int for Spinner list
     */
    private int mapFreqToInt(String freq) {
        int freqInt = SpinnerConstants.DAILY;
        switch (freq) {
            case "DAILY":{
                freqInt = SpinnerConstants.DAILY;
                break;
            }
            case "WEEKLY":{
                freqInt = SpinnerConstants.WEEKLY;
                break;
            }
            case "MONTHLY":{
                freqInt = SpinnerConstants.MONTHLY;
                break;
            }
            case "YEARLY":{
                freqInt = SpinnerConstants.YEARLY;
                break;
            }
        }
        return freqInt;
    }
    private void setUpArrayAdapter() {
        reminderArrayList = new ArrayList<>();
        arrayAdapter = new ReminderListArrayAdapter(this, R.layout.reminder_list_detail, reminderArrayList);
        reminderListView.setAdapter(arrayAdapter);

    }

    /**
     * setEventReminder
     * This method reads the shared preferences to get the calendar id and account name for the
     * application. It then creates an EventReminder object
     */
    private void setEventReminder() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        long calendarId = sharedPreferences.getLong(getString(R.string.saved_calendar_id),0);
        String accountName = sharedPreferences.getString(getString(R.string.saved_account_name),"");

        if (calendarId != 0 && StringUtil.isNotNullEmptyBlank(accountName)) {
            eventReminder = new EventReminder(getApplicationContext(), calendarId, accountName);
        } else {
            View view = this.getWindow().findViewById(android.R.id.content);
            Snackbar.make(view, "Unknown calendar error", Snackbar.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * ReminderAsyncTask
     * This class reads the calendar contract events table for the associated application calendar
     */
    private class ReminderAsyncTask extends AsyncTask<String, Integer, ArrayList<MyEvent>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            reminderArrayList.clear();
            arrayAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(ArrayList<MyEvent> events) {
            super.onPostExecute(events);

            if (events != null) {
                for (int i = 0; i < events.size(); i++) {
                    reminderArrayList.add(events.get(i));
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        protected ArrayList<MyEvent> doInBackground(String... params) {
            MyEvent[] list = eventReminder.queryEvents();

            if (list != null) {
                ArrayList<MyEvent> arrayList = new ArrayList<>();
                Collections.addAll(arrayList, list);

                return arrayList;
            } else {
                return null;
            }

        }
    }
}
