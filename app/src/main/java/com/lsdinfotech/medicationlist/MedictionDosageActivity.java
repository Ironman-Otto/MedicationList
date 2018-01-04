package com.lsdinfotech.medicationlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import data.Constants;
import utility.SpinnerConstants;
import utility.SpinnerUtil;
import utility.StringUtil;

public class MedictionDosageActivity extends AppCompatActivity {

    String                  dosage;
    String                  strengthValue;
    String                  unitValue;
    private EditText        strength;
    private Spinner         unit;
    private SpinnerUtil     unitSpinnerSetup;
    private String          ERROR_MESSAGE = "Please verify dosage values.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediction_dosage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpViews();
        setUpSpinners();

        Intent intent = getIntent();
        strengthValue = intent.getStringExtra(Constants.DOSAGE_STRENGTH_VALUE);
        unitValue = intent.getStringExtra(Constants.DOSAGE_UNIT_VALUE);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readScreenInput();
                if (verifyDosageValue()) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.DOSAGE, dosage);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Snackbar.make(view, ERROR_MESSAGE, Snackbar.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        strength.setText(strengthValue);
        if (StringUtil.isNullOrEmpty(unitValue)){
            unitSpinnerSetup.setSelection(Constants.DEFAULT_DOSAGE_UNIT);
        } else {
            unitSpinnerSetup.setSelection(unitValue);
        }
    }

    /**
     * setUpViews
     * This method sets the view to the proper values.
     */
    private void setUpViews() {
        strength    = (EditText)findViewById(R.id.medication_dosage_strength);
        unit        = (Spinner)findViewById(R.id.medication_dosage_unit);

    }

    /**
     * setUpSpinners
     * This method sets up the spinner for the dosage unit
     */
    private void setUpSpinners() {
        unitSpinnerSetup = new SpinnerUtil(MedictionDosageActivity.this, unit, SpinnerConstants.unitList,
                R.layout.my_spinner_item, R.layout.my_spinner_dropdown_item);
        unitSpinnerSetup.setUpSpinner(SpinnerConstants.NOSORT);
    }

    /**
     * readScreenInput
     * This method reads the strength and  unit from the screen
     */
    private void readScreenInput() {

        strengthValue   = strength.getText().toString().trim();
        unitValue       = unitSpinnerSetup.getSelection().trim();
    }

    /**
     * verityDosageValue
     * This method returns true if both strenght and unit are set with a value
     * dosage is formated first then the value test is performed. Dosage is ignored
     * if the verification returns false.
     * @return boolean
     */
    private boolean verifyDosageValue() {

        if (!StringUtil.isNullOrEmpty(strengthValue)) {
            dosage = strengthValue.trim() + " " + unitValue;
        }

        return !StringUtil.isNullOrEmpty(strengthValue) && !StringUtil.isNullOrEmpty(unitValue);

    }

}
