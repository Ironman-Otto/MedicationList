package com.lsdinfotech.medicationlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import data.Constants;
import data.Database;
import model.FamilyMember;
import model.Name;
import model.Physician;
import utility.StringUtil;

public class NameActivity extends AppCompatActivity {

    private TextView        title;
    private EditText        prefix;
    private EditText        firstName;
    private EditText        middleName;
    private EditText        lastName;
    private EditText        suffix;
    private Name            name;
    private int             mode;
    private int             nameType;
    private int             primaryKey;
    private FamilyMember    familyMember;
    private Physician       physician;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpViews();
        name = new Name();

        Intent intent   = getIntent();
        nameType        = intent.getIntExtra(Constants.NAME_TYPE, Constants.NO_NAME_TYPE);
        primaryKey      = intent.getIntExtra(Constants.PRIMARY_KEY, Constants.NO_PRIMARY_KEY);
        mode            = intent.getIntExtra(Constants.MODE, Constants.INSERT);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readScreenInput();
                if (verifyName()) {
                    int rc = 0;

                    switch (nameType) {
                        case Constants.NAME_PHYSICIAN:{
                            rc = processNameToPhysicianDb(mode, preparePhysician());
                            break;
                        }
                        case Constants.NAME_FAMILY_MEMBER:{
                            rc = processNameToFamilyDb(mode, prepareFamilyMember());
                            break;
                        }
                    }

                    if (rc > 0) {
                        setResult(RESULT_OK,prepareReturnNameIntent(rc, mode, primaryKey) );
                    } else {
                        setResult(RESULT_CANCELED);
                    }

                    Database.displayTransactionResults(rc, view, mode);
                    finish();
                }
                else {
                    Snackbar.make(view, getString(R.string.name_not_specified), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        switch (nameType){
            case Constants.NAME_PHYSICIAN: {
                title.setText(Constants.TITLE_FOR_PHYSICIAN_NAME);
                break;
            }
            case Constants.NAME_FAMILY_MEMBER: {
                title.setText(Constants.TITLE_FOR_FAMILY_MEMBER_NAME);
                break;
            }
        }

        readDbForName(mode, nameType);
    }

    /**
     * prepareReturnNameIntent
     * This method analyzes the mode and determines which
     * primanry key to return. On update we send back the original primary key
     * that was passed in
     * @param rc return from database access
     * @param mode insert or update
     * @param primaryKey key sent in on update
     * @return Intent with all necessary in
     */
    private Intent prepareReturnNameIntent(int rc, int mode, int primaryKey) {
        Intent intent = new Intent();
        intent.putExtra(Constants.RETURNED_NAME, createjsonObjectForName());

        intent.putExtra(Constants.NAME_TYPE, nameType);
        if (mode == Constants.UPDATE) {
            intent.putExtra(Constants.PRIMARY_KEY, primaryKey);
        } else {
            intent.putExtra(Constants.PRIMARY_KEY, rc);
        }

        return intent;
    }
    /**
     * setUpViews
     * This method sets up the view for the activity
     */
    private void setUpViews() {
        title           = (TextView)findViewById(R.id.name_title);
        prefix          = (EditText)findViewById(R.id.name_prefix);
        firstName       = (EditText)findViewById(R.id.name_first_name);
        middleName      = (EditText)findViewById(R.id.name_middle_name);
        lastName        = (EditText)findViewById(R.id.name_last_name);
        suffix          = (EditText)findViewById(R.id.name_suffix);

    }

    /**
     * readScreenInput
     * This method reads the name from the screen
     * and captializes the string
     */
    private void readScreenInput(){
        name.setNamePrefix(StringUtil.capFirstCharString(prefix.getText().toString().trim()));
        name.setFirstName(StringUtil.capFirstCharString(firstName.getText().toString().trim()));
        name.setMiddleName(StringUtil.capFirstCharString(middleName.getText().toString().trim()));
        name.setLastName(StringUtil.capFirstCharString(lastName.getText().toString().trim()));
        name.setNameSuffix(StringUtil.capFirstCharString(suffix.getText().toString().trim()));
    }

    /**
     * verifyName
     * This method verifies that the name has at least the last name
     * entered. True if valid last name. False otherwise.
     * @return Boolean
     */
    private boolean verifyName() {

        return StringUtil.isNotNullEmptyBlank(name.getLastName())||
                StringUtil.isNotNullEmptyBlank(name.getFirstName())||
                StringUtil.isNotNullEmptyBlank(name.getMiddleName());
    }

    /**
     * processNameToFamilyDb
     * This method inserts the name into the Family Member table
     */
    private int processNameToFamilyDb(int mode, FamilyMember familyMember ) {

        int rowId;

        if ( mode == Constants.INSERT ) {
            rowId = Database.familyTable.insert(familyMember);
        }
        else {
            rowId = Database.familyTable.update(familyMember);
        }

        return rowId;

    }

    /**
     * prepareFamilyMember
     * This method prepares a family member for the database
     * @return FamilyMember
     */
    private FamilyMember prepareFamilyMember() {
        FamilyMember familyMember = new FamilyMember();
        familyMember.setSuffix(name.getNameSuffix());
        familyMember.setFirstName(name.getFirstName());
        familyMember.setMiddleName(name.getMiddleName());
        familyMember.setLastName(name.getLastName());
        familyMember.setPrefix(name.getNamePrefix());

        if (mode == Constants.UPDATE) {
            familyMember.setPrimaryKey(primaryKey);
        }

        return familyMember;
    }

    /**
     * processNameToPhysicianDb
     * This method inserts the name into the Physician table
     */
    private int processNameToPhysicianDb(int mode, Physician physician) {

        int rowId;

        if ( mode == Constants.INSERT ) {
            rowId = Database.physicianTable.insert(physician);
        }
        else {
            rowId = Database.physicianTable.update(physician);
        }

        return rowId;

    }

    /**
     * preparePhysician
     * This method populates the Physician object with name information
     * @return Physician
     */
    private Physician preparePhysician() {
        Physician physician = new Physician();
        physician.setSuffix(name.getNameSuffix());
        physician.setFirstName(name.getFirstName());
        physician.setMiddleName(name.getMiddleName());
        physician.setLastName(name.getLastName());
        physician.setPrefix(name.getNamePrefix());

        if (mode == Constants.UPDATE) {
            physician.setPrimaryKey(primaryKey);
        }

        return physician;
    }

    /**
     * createjsonObjectForName
     * This method creates a json object in string
     * @return jsonobject string
     */
    private String createjsonObjectForName() {
        Gson gson = new Gson();
        return gson.toJson(name);
    }

    /**
     * readDdForName
     * This method reads the appropriate database to populate the screen depending
     * on the name type and mode equal to UPDATE
     * @param mode UPDATE or INSERT
     * @param nameType family member or physician
     */
    private void readDbForName(int mode, int nameType) {
        if ( mode == Constants.UPDATE ) {
            switch (nameType) {
                case Constants.NAME_FAMILY_MEMBER: {
                    ArrayList<FamilyMember> familyMembers = Database.familyTable.queryFamilyMembers(false, primaryKey, null);
                    if (familyMembers.size() == 1) {
                        familyMember = familyMembers.get(0);
                    }
                    break;
                }
                case Constants.NAME_PHYSICIAN:{
                    ArrayList<Physician> physicians = Database.physicianTable.queryPhysicians(false, primaryKey, null);
                    if (physicians.size() == 1) {
                        physician = physicians.get(0);
                    }
                    break;
                }
            }
            if ( familyMember != null || physician != null) {
                fillScreenFromDb(nameType);
            }
        }
    }

    /**
     * fillScreenFromDb
     * This method is called when in mode is UPDATE
     * The database has already been called. The data is
     * sent to the screen
     * @param nameType family member or physician
     */
    private void fillScreenFromDb(int nameType) {
        switch (nameType){
            case Constants.NAME_FAMILY_MEMBER:{
                populateNameFamilyMember();
                break;
            }
            case Constants.NAME_PHYSICIAN:{
                populateNamePhysician();
                break;
            }
        }
    }

    /**
     * populateNamaeFamilyMember
     * This method populates the screen with the name of the family member
     * This method is called when mode is UPDATE and nametype is  FAMILY
     */
    private void populateNameFamilyMember() {
        firstName.setText(familyMember.getFirstName());
        middleName.setText(familyMember.getMiddleName());
        lastName.setText(familyMember.getLastName());
        suffix.setText(familyMember.getSuffix());
        prefix.setText(familyMember.getPrefix());
    }

    /**
     * populateNamePhysician
     * This method populates the screen with the name of the physician
     * This method is called when mode is updata and nametype is PHYSICIAN
     */
    private void populateNamePhysician() {
        firstName.setText(physician.getFirstName());
        middleName.setText(physician.getMiddleName());
        lastName.setText(physician.getLastName());
        suffix.setText(physician.getSuffix());
        prefix.setText(physician.getPrefix());
    }
}
