package com.lsdinfotech.medicationlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import utility.ErrorUtil;
import utility.MyCalendar;

public class MainActivity extends AppCompatActivity {

    TextView    medicationSelection;
    TextView    familyMemberSelection;
    TextView    physicianSelection;
    TextView    reminderSelection;
    TextView    help;
    TextView    contactUs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.lsd_logo_200_65);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        setUpViews();
        setUpEventHandlers();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        ErrorUtil.confirmExit(false, this);
    }

    /**
     * setUpViews
     * This method sets up the views for the activity
     */
    private void setUpViews() {
        medicationSelection     = (TextView) findViewById(R.id.main_medication_tv_btn);
        familyMemberSelection   = (TextView) findViewById(R.id.main_family_member_tv_btn);
        physicianSelection      = (TextView) findViewById(R.id.main_physician_tv_btn);
        reminderSelection       = (TextView) findViewById(R.id.main_reminder_tv_btn);
        help                    = (TextView) findViewById(R.id.main_help);
        contactUs               = (TextView) findViewById(R.id.main_contact);
    }

    /**
     * setUpEventHandlers
     * This method sets up the onClickListeners for the main function list
     */
    private void setUpEventHandlers() {
        medicationSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MedicationListActivity.class);
                startActivity(intent);
            }
        });

        familyMemberSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FamilyMemberListActivity.class);
                startActivity(intent);
            }
        });

        physicianSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PhysicianListActivity.class);
                startActivity(intent);
            }
        });

        reminderSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReminderListActivity.class);
                startActivity(intent);
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
                startActivity(intent);
            }
        });

        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","medlist@lsdinfotech.com", null));
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });
    }

}
