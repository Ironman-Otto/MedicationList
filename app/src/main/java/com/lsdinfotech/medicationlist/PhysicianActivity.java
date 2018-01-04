package com.lsdinfotech.medicationlist;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import model.Name;
import model.Physician;
import utility.KeyBoardUtil;
import utility.PhoneUtil;
import utility.PhysicianInputDialog;
import utility.StringUtil;

public class PhysicianActivity extends AppCompatActivity {

    private TextView            physicianName;
    private ImageButton         physicianNameBtn;
    private TextView            physicanSpecialty;
    private ImageButton         physicianSpecialtyBtn;
    private TextView            physicianPhone;
    private ImageButton         physicianPhoneBtn;
    private TextView            physicianAddress;
    private ImageButton         physicianAddressBtn;
    private TextView            physicianAddress2;
    private TextView            physicianCity;
    private TextView            physicianState;
    private TextView            physicianZip;
    private TextView            physicianFax;
    private ImageButton         physicianFaxBtn;
    private TextView            physicianEmail;
    private ImageButton         physicianEmailBtn;

    private Physician           physician;
    private int                 mode;
    private int                 physicianPrimaryKey;
    private boolean             firstTime = true;
    private static final int    REQUEST_CAMERA_PERMISSION = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physician);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setUpViews();
        setUpEventHandlers();

        physician = new Physician();

        Intent intent = getIntent();
        mode = intent.getIntExtra(Constants.MODE, Constants.UPDATE);
        if ( mode == Constants.UPDATE ){
            physicianPrimaryKey = intent.getIntExtra(Constants.PRIMARY_KEY, Constants.NO_PRIMARY_KEY);
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
        if (mode == Constants.UPDATE && physicianPrimaryKey > Constants.NO_PRIMARY_KEY && firstTime) {
            ArrayList<Physician> physicians = Database.physicianTable.queryPhysicians(false, physicianPrimaryKey, null);
            physician = physicians.get(0);
            fillScreenFromDb();

        }
        firstTime = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_physician, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_delete:
                Database.physicianTable.delete(physician.getPrimaryKey());
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new PhysicianInputDialog(physicianPhone, PhysicianActivity.this, getApplicationContext()).getPhone().show();
                }
                break;
            }
        }
    }

    /**
     * populateNamePriKey
     * This method sets the proper name view depending on the
     * type of name being returned to the activity. This method also
     * sets the proper primary key for the physician
     * @param nameType int
     * @param name name that was returned
     * @param primaryKey value of new primary key from name
     */
    private void populateNamePriKey(int nameType, Name name, int primaryKey) {
        switch (nameType) {
            case Constants.NAME_PHYSICIAN: {
                physicianName.setText(name.toString());
                physician.setPrimaryKey(primaryKey);
                populatePhysicianName(name);
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
        physicianName               = (TextView) findViewById(R.id.physician_name);
        physicianNameBtn            = (ImageButton)findViewById(R.id.physician_name_button);
        physicanSpecialty           = (TextView) findViewById(R.id.physician_specialty);
        physicianSpecialtyBtn       = (ImageButton) findViewById(R.id.physician_specialy_button);
        physicianPhone              = (TextView) findViewById(R.id.physician_phone);
        physicianPhoneBtn           = (ImageButton) findViewById(R.id.physician_phone_button);
        physicianAddress            = (TextView) findViewById(R.id.physician_address);
        physicianAddressBtn         = (ImageButton) findViewById(R.id.physician_address_button);
        physicianAddress2           = (TextView) findViewById(R.id.physician_address2);
        physicianCity               = (TextView) findViewById(R.id.physician_city);
        physicianState              = (TextView) findViewById(R.id.physician_state);
        physicianZip                = (TextView) findViewById(R.id.physician_zip);
        physicianFax                = (TextView) findViewById(R.id.physician_fax);
        physicianFaxBtn             = (ImageButton) findViewById(R.id.physician_fax_button);
        physicianEmail              = (TextView) findViewById(R.id.physician_email);
        physicianEmailBtn           = (ImageButton) findViewById(R.id.physician_email_button);

    }

    /**
     * setUpEventHandlers
     * This method sets up all the event handlers for the buttons on thescreen
     */
    private void setUpEventHandlers() {
        physicianNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(prepareNameRequestIntent(), Constants.NAME_REQUEST);
            }
        });

        physicianSpecialtyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PhysicianInputDialog(physicanSpecialty, PhysicianActivity.this, getApplicationContext()).getSpecialty().show();
            }
        });

        physicianPhoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(PhysicianActivity.this,
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    new PhysicianInputDialog(physicianPhone, PhysicianActivity.this, getApplicationContext()).getPhone().show();
                } else {
                    ActivityCompat.requestPermissions(PhysicianActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CAMERA_PERMISSION);
                }
            }
        });

        physicianAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhysicianInputDialog address = new PhysicianInputDialog(null, PhysicianActivity.this, getApplicationContext());
                address.setAddressViews(physicianAddress, physicianAddress2, physicianCity, physicianState, physicianZip);
                address.getAddress().show();

            }
        });

        physicianFaxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PhysicianInputDialog(physicianFax, PhysicianActivity.this, getApplicationContext()).getFax().show();
            }
        });

        physicianEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PhysicianInputDialog(physicianEmail, PhysicianActivity.this, getApplicationContext()).getEmail().show();
            }
        });

        physicianEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto",physicianEmail.getText().toString(), null));
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
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
            long rc = processPhysician(mode, physician);
            Database.displayTransactionResults(rc, view, mode);
            if (mode == Constants.UPDATE) {
                PhysicianActivity.this.finish();
            } else {
                clearScreen();
                KeyBoardUtil.hideKeyboard(PhysicianActivity.this);
            }
        } else {
            Snackbar.make(view, R.string.name_not_specified, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * validateInput
     * This method verifies that a name has been entered.
     * A name is required
     * @return true if there is a name otherwise false
     */
    private boolean validateInput() {
        return (StringUtil.isNotNullEmptyBlank(physician.getFirstName()) ||
                StringUtil.isNotNullEmptyBlank(physician.getMiddleName()) ||
                StringUtil.isNotNullEmptyBlank(physician.getLastName()));
    }
    /**
     * clearScreen
     * This method clears the screen of all previous input
     */
    private void clearScreen() {
        physicianName.setText("");
        physicanSpecialty.setText("");
        physicianPhone.setText("");
        physicianAddress.setText("");
        physicianAddress2.setText("");
        physicianCity.setText("");
        physicianZip.setText("");
        physicianEmail.setText("");
        physicianFax.setText("");
    }

    /**
     * readScreenInput
     * This method reads the screen values
     */
    private void readScreenInput() {
        physician.setSpecialty(physicanSpecialty.getText().toString().trim());
        physician.setPhone(PhoneUtil.unFormatPhoneNumber(physicianPhone.getText().toString().trim()));
        physician.setAddress1(physicianAddress.getText().toString().trim());
        physician.setAddress2(physicianAddress2.getText().toString().trim());
        physician.setCity(physicianCity.getText().toString().trim());
        physician.setState(physicianState.getText().toString().trim());
        physician.setZipcode(physicianZip.getText().toString().trim());
        physician.setEmail(physicianEmail.getText().toString().trim());
        physician.setFax(PhoneUtil.unFormatPhoneNumber(physicianFax.getText().toString().trim()));

    }

    /**
     * fillScreenFromDb
     * This method fills the activity screen with the values set in the
     * database for the specific physician that is being updated.
     */
    private void fillScreenFromDb() {
        physicianName.setText(physician.toString());
        physicanSpecialty.setText(physician.getSpecialty());
        physicianPhone.setText(PhoneUtil.formatPhoneNumber(getApplicationContext(),physician.getPhone()));
        physicianAddress.setText(physician.getAddress1());
        physicianAddress2.setText(physician.getAddress2());
        physicianCity.setText(physician.getCity());
        physicianState.setText(physician.getState());
        physicianZip.setText(physician.getZipcode());
        physicianEmail.setText(physician.getEmail());
        physicianFax.setText(PhoneUtil.formatPhoneNumber(getApplicationContext(),physician.getFax()));
    }

    /**
     * processPhysician
     * This method sends the input from screen to the database
     * @return long rowid of record accessed
     */
    private long processPhysician(int mode, Physician physician) {
        long rowId;

        if ( mode == Constants.INSERT ) {
            rowId = Database.physicianTable.insert(physician);
        }
        else {
            rowId = Database.physicianTable.update(physician);
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
            intent.putExtra(Constants.NAME_TYPE, Constants.NAME_PHYSICIAN);
            intent.putExtra(Constants.PRIMARY_KEY, physician.getPrimaryKey());
        }
        return intent;
    }

    /**
     * populatePhysicianName
     * This method populates the name fields from the Name
     * returned from the NAME_REQUEST
     * @param name class from NameActivity
     */
    private void populatePhysicianName(Name name) {
        physician.setPrefix(name.getNamePrefix().trim());
        physician.setFirstName(name.getFirstName().trim());
        physician.setMiddleName(name.getMiddleName().trim());
        physician.setLastName(name.getLastName().trim());
        physician.setSuffix(name.getNameSuffix().trim());
    }


}
