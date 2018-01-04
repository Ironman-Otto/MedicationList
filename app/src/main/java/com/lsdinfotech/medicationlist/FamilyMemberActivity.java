package com.lsdinfotech.medicationlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import data.Constants;
import data.Database;
import model.FamilyMember;
import model.Name;
import utility.FamilyInputDialog;
import utility.KeyBoardUtil;
import utility.StringUtil;
import utility.TimeDatePickers;

public class FamilyMemberActivity extends AppCompatActivity {

    private TextView        familyMemberName;
    private ImageButton     familyMemberNameBtn;
    private TextView        familyMemberBDay;
    private ImageButton     familyMemberBDayBtn;
    private TextView        familyMemberGender;
    private ImageButton     familyMemberGenderBtn;
    private TextView        familyMemberHeight;
    private ImageButton     familyMemberHeightBtn;
    private TextView        familyMemberWeight;
    private ImageButton     familyMemberWeightBtn;
    private TextView        familyMemberBloodType;
    private ImageButton     familyMemberBloodTypeBtn;

    private FamilyMember    familyMember;
    private int             mode;
    private int             familyMemberPrimaryKey;
    private boolean         firstTime = true;
    private TimeDatePickers timeDatePickers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_member);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setUpViews();
        setUpEventHandlers();

        familyMember = new FamilyMember();
        timeDatePickers = new TimeDatePickers(this, Constants.DATE_FORMAT, Constants.TIME_FORMAT);

        Intent intent = getIntent();
        mode = intent.getIntExtra(Constants.MODE, Constants.UPDATE);
        if ( mode == Constants.UPDATE ){
            familyMemberPrimaryKey = intent.getIntExtra(Constants.PRIMARY_KEY, Constants.NO_PRIMARY_KEY);
        }

    }

    @Override
    protected void onResume() {

        super.onResume();
        if (mode == Constants.UPDATE && familyMemberPrimaryKey > Constants.NO_PRIMARY_KEY && firstTime) {
            // read the database for the family member to be updated first time into activity
            ArrayList<FamilyMember> familyMembers = Database.familyTable.queryFamilyMembers(false, familyMemberPrimaryKey, null);
            familyMember = familyMembers.get(0);
            fillScreenFromDb();

        }
        firstTime = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_family_member, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_delete:
                Database.familyTable.delete(familyMember.getPrimaryKey());
                Database.medTable.updateMedicationForeignKey(familyMember.getPrimaryKey(), Constants.NAME_FAMILY_MEMBER);
                finish();
                break;
            case R.id.action_save:{
                processSaveAction();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.NAME_REQUEST: {
                    Gson gson = new Gson();
                    Name name = gson.fromJson(data.getStringExtra(Constants.RETURNED_NAME), Name.class);
                    populateNamePriKey(data.getIntExtra(Constants.NAME_TYPE, Constants.NO_NAME_TYPE), name,
                            data.getIntExtra(Constants.PRIMARY_KEY, Constants.NO_PRIMARY_KEY));
                    break;
                }
            }
        }

    }

    /**
     * populateNamePriKey
     * This method sets the proper name view depending on the
     * type of name being returned to the activity. This method also
     * sets the proper primary key for the family member or physician
     * @param nameType int
     * @param name name that was returned
     * @param primaryKey value of new primary key from name
     */
    private void populateNamePriKey(int nameType, Name name, int primaryKey) {
        switch (nameType) {
            case Constants.NAME_FAMILY_MEMBER:{
                familyMemberName.setText(name.toString());
                familyMember.setPrimaryKey(primaryKey);
                populateFamilyMemberName(name);
                break;
            }
            default:{

            }
        }
    }

    /**
     * setUpViews
     * This method sets up all the views for the activity
     */
    private void setUpViews() {
        familyMemberName            = (TextView) findViewById(R.id.family_member_name);
        familyMemberNameBtn         = (ImageButton) findViewById(R.id.family_member_name_button);
        familyMemberBDay            = (TextView) findViewById(R.id.family_member_birthday);
        familyMemberBDayBtn         = (ImageButton) findViewById(R.id.family_member_birthday_button);
        familyMemberGender          = (TextView) findViewById(R.id.family_member_gender);
        familyMemberGenderBtn       = (ImageButton) findViewById(R.id.family_member_gender_button);
        familyMemberHeight          = (TextView) findViewById(R.id.family_member_height);
        familyMemberHeightBtn       = (ImageButton) findViewById(R.id.family_member_height_button);
        familyMemberWeight          = (TextView) findViewById(R.id.family_member_weight);
        familyMemberWeightBtn       = (ImageButton) findViewById(R.id.family_member_weight_button);
        familyMemberBloodType       =(TextView) findViewById(R.id.family_member_blood_type);
        familyMemberBloodTypeBtn    = (ImageButton) findViewById(R.id.family_member_blood_type_button);


    }

    /**
     * setUpEventHandlers
     * This method sets up all the event handlers for the buttons on thescreen
     */
    private void setUpEventHandlers() {
        familyMemberNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(prepareNameRequestIntent(), Constants.NAME_REQUEST);
            }
        });

        familyMemberBDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeDatePickers.showDatePicker(familyMemberBDay);
            }
        });

        familyMemberGenderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FamilyInputDialog(familyMemberGender, FamilyMemberActivity.this, getApplicationContext()).getGender().show();
            }
        });

        familyMemberHeightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FamilyInputDialog(familyMemberHeight, FamilyMemberActivity.this, getApplicationContext()).getHeight().show();
            }
        });

        familyMemberWeightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FamilyInputDialog(familyMemberWeight,FamilyMemberActivity.this, getApplicationContext()).getWeight().show();
            }
        });

        familyMemberBloodTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FamilyInputDialog(familyMemberBloodType, FamilyMemberActivity.this, getApplicationContext()).getBloodType().show();
            }
        });
    }

    /**
     * processSaveAction
     * This method handles the save button on the action toolbar it reads and
     * validates screen input and inserts or updates medication in database
     */
    private void processSaveAction() {
        View view = this.getWindow().findViewById(android.R.id.content);
        readScreenInput();
        if (validateInput()) {
            long rc = processFamilyMember(mode, familyMember);
            Database.displayTransactionResults(rc, view, mode);
            if (mode == Constants.UPDATE) {
                FamilyMemberActivity.this.finish();
            } else {
                clearScreen();
                KeyBoardUtil.hideKeyboard(FamilyMemberActivity.this);
            }
        } else {
            Snackbar.make(view, R.string.name_not_entered_msg, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * validateInput
     * This method verifies that there is a name given
     * @return true if there is name false if not
     */
    private boolean validateInput() {
        return (StringUtil.isNotNullEmptyBlank(familyMember.getFirstName()) ||
                StringUtil.isNotNullEmptyBlank(familyMember.getMiddleName()) ||
                StringUtil.isNotNullEmptyBlank(familyMember.getLastName()));
    }
    /**
     * clearScreen
     * This method clears the screen of all previous input
     */
    private void clearScreen() {
        familyMemberName.setText("");
        familyMemberBDay.setText("");
        familyMemberGender.setText("");
        familyMemberHeight.setText("");
        familyMemberWeight.setText("");
        familyMemberBloodType.setText("");
    }

    /**
     * readScreenInput
     * This method reads the screen values
     */
    private void readScreenInput() {
        familyMember.setBirthdate(familyMemberBDay.getText().toString().trim());
        familyMember.setGender(familyMemberGender.getText().toString().trim());
        familyMember.setHeight(familyMemberHeight.getText().toString().trim());
        familyMember.setWeight(familyMemberWeight.getText().toString().trim());
        familyMember.setBloodType(familyMemberBloodType.getText().toString().trim());
    }

    /**
     * fillScreenFromDb
     * This method fills the activity screen with the values set in the
     * database for the specific medication that is being updated.
     */
    private void fillScreenFromDb() {
        familyMemberName.setText(familyMember.toString());
        familyMemberBDay.setText(familyMember.getBirthdate());
        familyMemberGender.setText(familyMember.getGender());
        familyMemberHeight.setText(familyMember.getHeight());
        familyMemberWeight.setText(familyMember.getWeight());
        familyMemberBloodType.setText(familyMember.getBloodType());
    }

    /**
     * processFamilyMember
     * This method sends the input from screen to the database
     * @return long rowid of record accessed
     */
    private long processFamilyMember(int mode, FamilyMember familyMember) {
        long rowId;

        if ( mode == Constants.INSERT ) {
            rowId = Database.familyTable.insert(familyMember);
        }
        else {
            rowId = Database.familyTable.update(familyMember);
        }

        return rowId;
    }

    /**
     * prepareNameRequestIntent
     * This method prepares the intent to get name set
     * @return Intent to send to NameActivity
     */
    private Intent prepareNameRequestIntent() {
        Intent intent =  new Intent(getApplicationContext(), NameActivity.class);
        if (mode == Constants.UPDATE) {
            intent.putExtra(Constants.MODE, Constants.UPDATE);
            intent.putExtra(Constants.NAME_TYPE, Constants.NAME_FAMILY_MEMBER);
            intent.putExtra(Constants.PRIMARY_KEY, familyMember.getPrimaryKey());
        }
        return intent;
    }

    /**
     * populateFamilyMemberName
     * This method populates the familymember name fields from the Name
     * returned from the NAME_REQUEST
     * @param name class from NameActivity
     */
    private void populateFamilyMemberName(Name name) {
        familyMember.setPrefix(name.getNamePrefix().trim());
        familyMember.setFirstName(name.getFirstName().trim());
        familyMember.setMiddleName(name.getMiddleName().trim());
        familyMember.setLastName(name.getLastName().trim());
        familyMember.setSuffix(name.getNameSuffix().trim());
    }
}
