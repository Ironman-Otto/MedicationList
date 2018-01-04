package com.lsdinfotech.medicationlist;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import data.Constants;
import data.Database;
import utility.SpinnerConstants;
import model.Medication;
import model.Name;
import utility.ImageUtility;
import utility.KeyBoardUtil;
import utility.SpinnerUtil;
import utility.StringUtil;

public class MedicationActivity extends AppCompatActivity {

    private ImageView           medImage;
    private TextView            medImageCamera;
    private ImageView           prescLabelImage;
    private TextView            prescLabelCamera;
    private TextView            medicationName;
    private EditText            medicationBrandName;
    private EditText            medicationMfg;
    private EditText            medicationTakeInstr;
    private Spinner             routeSpinner;
    private Spinner             formSpinner;
    private SpinnerUtil         routeSpinnerSetup;
    private SpinnerUtil         formSpinnerSetup;
    private TextView            strength;
    private ImageButton         strengthButton;
    private EditText            quantity;
    private TextView            familyMember;
    private ImageButton         familyMemberButton;
    private TextView            physician;
    private ImageButton         physicianButton;
    private EditText            pharmacy;
    private EditText            prescriptionNumber;
    private EditText            note;

    private Medication          medication;
    private int                 mode;
    private int                 medicationPrimaryKey;
    private boolean             firstTime = true;
    private int                 imageType;
    private final int           IMAGE_LABEL = 2;
    private final int           IMAGE_MEDICATION = 1;
    private final static int    REQUEST_CAMERA_PERMISSION = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setUpViews();
        setUpEventHandlers();
        setUpSpinners();

        medication = new Medication();

        Intent intent = getIntent();
        mode = intent.getIntExtra(Constants.MODE, Constants.UPDATE);
        if ( mode == Constants.UPDATE ){
            medicationPrimaryKey = intent.getIntExtra(Constants.PRIMARY_KEY, Constants.NO_PRIMARY_KEY);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_medication_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_delete:
                Database.medTable.delete(medication.getPrimaryKey());
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
    protected void onResume() {

        super.onResume();
        if (mode == Constants.UPDATE && medicationPrimaryKey > Constants.NO_PRIMARY_KEY && firstTime) {
            ArrayList<Medication> medications = Database.medTable.queryMedications(false, medicationPrimaryKey, null);
            medication = medications.get(0);
            fillScreenFromDb();

        }
        firstTime = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_DOSAGE: {
                    strength.setText(data.getStringExtra(Constants.DOSAGE));
                    break;
                }
                case Constants.NAME_REQUEST: {
                    Gson gson = new Gson();
                    Name name = gson.fromJson(data.getStringExtra(Constants.RETURNED_NAME), Name.class);
                    populateNamePriKey(data.getIntExtra(Constants.NAME_TYPE, Constants.NO_NAME_TYPE), name,
                                        data.getIntExtra(Constants.PRIMARY_KEY, Constants.NO_PRIMARY_KEY));
                    break;
                }
                case Constants.REQUEST_CAMERA:{
                    processCameraRequest(data);
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
                    processImageIntent();
                }
            }
        }

    }

    /**
     * processCameraRequest
     * This method processes the return data from the camera request
     * There are two possible cases. The request could be for a label image
     * or a medication image. That is determined by the image type which is
     * set at the time the camera request is made. When the processing is done
     * the image type is reset to clear
     * @param data intent data containing image bitmap
     */
    private void processCameraRequest(Intent data) {
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        switch (imageType) {
            case IMAGE_LABEL:{
                prescLabelImage.setImageBitmap(bitmap);
                medication.setPrescrLabelImage(bitmap);
                imageType = 0;
                break;
            }
            case IMAGE_MEDICATION:{
                medImage.setImageBitmap(bitmap);
                medication.setMedicationImage(bitmap);
                imageType = 0;
                break;
            }
        }
    }
    /**
     * processSaveAction
     * This method handles the save button on the action toolbar it reads and
     * validates screen input and inserts or updates medication in database
     * invalid input is blank generic and brand names. Message is returned to user
     * and activity remains in state
     */
    private void processSaveAction() {
        View view = this.getWindow().findViewById(android.R.id.content);

        readScreenInput();
        if(validateInput()) {
            long rc = processMedication(mode, medication);
            Database.displayTransactionResults(rc, view, mode);
            MedicationActivity.this.finish();
        } else {
            Snackbar.make(view, R.string.medication_not_entered_msg, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * validateInput
     * This method validates the input by determining if either
     * the brand name or generic name is filled in
     *
     * @return true if brand or generic is filled in false if both are not
     */
    private boolean validateInput() {
        return  (StringUtil.isNotNullEmptyBlank(medication.getName()) ||
            StringUtil.isNotNullEmptyBlank(medication.getBrandName()));


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
                familyMember.setText(name.toString());
                medication.setFamilyMemberPid(primaryKey);
                break;
            }
            case Constants.NAME_PHYSICIAN: {
                physician.setText(name.toString());
                medication.setPhysicianPid(primaryKey);
                break;
            }
            default:{

            }
        }
    }

    /**
     * setUpViews
     * This method sets up all views used by this activity
     */
    private void setUpViews() {

        medImage                = (ImageView)findViewById(R.id.med_image);
        medImageCamera          = (TextView)findViewById(R.id.med_image_text);
        prescLabelImage         = (ImageView)findViewById(R.id.med_prescription_label_image);
        prescLabelCamera        = (TextView) findViewById(R.id.med_label_image_text);
        medicationName          = (TextView)findViewById(R.id.med_name_input);
        routeSpinner            = (Spinner)findViewById(R.id.med_route_spinner);
        formSpinner             = (Spinner)findViewById(R.id.med_form_spinner);
        strength                = (TextView)findViewById(R.id.med_strength);
        strengthButton          = (ImageButton)findViewById(R.id.med_strength_button);
        quantity                = (EditText)findViewById(R.id.med_qty);
        familyMember            = (TextView)findViewById(R.id.med_family_member_name);
        familyMemberButton      = (ImageButton)findViewById(R.id.med_family_member_button);
        physician               = (TextView)findViewById(R.id.med_dr_name);
        physicianButton         = (ImageButton) findViewById(R.id.med_physician_button);
        pharmacy                = (EditText)findViewById(R.id.med_pharmacy);
        prescriptionNumber      = (EditText)findViewById(R.id.med_script_num);
        note                    = (EditText)findViewById(R.id.med_notes);
        medicationBrandName     = (EditText)findViewById(R.id.med_brand_name_input);
        medicationMfg           = (EditText)findViewById(R.id.med_mfg);
        medicationTakeInstr     = (EditText)findViewById(R.id.med_take_instructions);

    }

    /**
     * checkCamerPermission
     * verify we have permission to use camera
     * @return true if we have premission
     */
    private boolean checkCameraPermisssion() {
        return (ContextCompat.checkSelfPermission(MedicationActivity.this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED);
    }
    /**
     * setUpEventHandlers
     * This method setsup all the event handlers associated with the views.
     */
    private void setUpEventHandlers() {
        strengthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(prepareCurrentDosageIntent(), Constants.REQUEST_DOSAGE);
            }
        });

        familyMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(prepareFamilyMemberIntent(), Constants.NAME_REQUEST);
            }
        });

        physicianButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(preparePhysicianIntent(), Constants.NAME_REQUEST);
            }
        });

        prescLabelCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageType = IMAGE_LABEL;
                if(checkCameraPermisssion()) {
                    processImageIntent();
                } else {
                    ActivityCompat.requestPermissions(MedicationActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            REQUEST_CAMERA_PERMISSION);
                }
            }
        });

        prescLabelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (medication.getPrescrLabelImage() != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    medication.getPrescrLabelImage().compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    Intent intent = new Intent(getApplicationContext(), ImageDisplayActivity.class);
                    intent.putExtra("image", byteArray);
                    startActivity(intent);
                }
            }
        });

        medImageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageType = IMAGE_MEDICATION;
                if(checkCameraPermisssion()) {
                    processImageIntent();
                } else {
                    ActivityCompat.requestPermissions(MedicationActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            REQUEST_CAMERA_PERMISSION);
                }
            }
        });

        medImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (medication.getMedicationImage() != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    medication.getMedicationImage().compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    Intent intent = new Intent(getApplicationContext(), ImageDisplayActivity.class);
                    intent.putExtra("image", byteArray);
                    startActivity(intent);
                }
            }

        });

    }

    /**
     * processImageIntent
     * Call for the camera to take picture
     *
     */
    private void processImageIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, Constants.REQUEST_CAMERA);
    }

    /**
     * setUpSpinners
     * This method sets up the spinners on the activity screen
     */
    private void setUpSpinners() {
        routeSpinnerSetup = new SpinnerUtil(MedicationActivity.this, routeSpinner, SpinnerConstants.routeList,
                R.layout.my_spinner_item, R.layout.my_spinner_dropdown_item);
        routeSpinnerSetup.setUpSpinner(SpinnerConstants.NOSORT);

        formSpinnerSetup = new SpinnerUtil(MedicationActivity.this, formSpinner, SpinnerConstants.formList,
                R.layout.my_spinner_item, R.layout.my_spinner_dropdown_item);
        formSpinnerSetup.setUpSpinner(SpinnerConstants.NOSORT);
    }

    /**
     * readScreenInput
     * This method reads the screen input and fills the medication object
     * to be inserted in database
     */
    private void readScreenInput() {

        medication.setName(StringUtil.capFirstCharString(medicationName.getText().toString().trim()));
        medication.setRoute(routeSpinnerSetup.getSelection().trim());
        medication.setForm((formSpinnerSetup.getSelection()).trim());
        medication.setStrength(strength.getText().toString().trim());
        medication.setQuantity(quantity.getText().toString().trim());
        medication.setFamilyMemberName(familyMember.getText().toString().trim());
        medication.setPhysicianName(physician.getText().toString().trim());
        medication.setPharmacy(StringUtil.capFirstCharString(pharmacy.getText().toString().trim()));
        medication.setPrescriptionNumber(prescriptionNumber.getText().toString().trim());
        medication.setNote(note.getText().toString().trim());
        medication.setBrandName(medicationBrandName.getText().toString().trim());
        medication.setManufacturer(medicationMfg.getText().toString().trim());
        medication.setTakeInstruct(medicationTakeInstr.getText().toString().trim());
    }

    /**
     * fillScreenFromDb
     * This method fills the activity screen with the values set in the
     * database for the specific medication that is being updated.
     */
    private void fillScreenFromDb() {
        medicationName.setText(medication.getName());
        setRouteSpinnerSelection();
        setFormSpinnerSelection();
        strength.setText(medication.getStrength());
        quantity.setText(medication.getQuantity());
        familyMember.setText(medication.getFamilyMemberName());
        physician.setText(medication.getPhysicianName());
        pharmacy.setText(medication.getPharmacy());
        prescriptionNumber.setText(medication.getPrescriptionNumber());
        note.setText(medication.getNote());
        if (medication.getPrescrLabelImage() != null) {
            prescLabelImage.setImageBitmap(medication.getPrescrLabelImage());
        }
        if (medication.getMedicationImage() != null) {
            medImage.setImageBitmap(medication.getMedicationImage());
        }
        medicationBrandName.setText(medication.getBrandName());
        medicationMfg.setText(medication.getManufacturer());
        medicationTakeInstr.setText(medication.getTakeInstruct());
    }

    /**
     * clearScreen
     * This method clears the activity screen
     */
    private void clearScreen() {
        medicationName.getEditableText().clear();
        routeSpinnerSetup.resetSpinner();
        formSpinnerSetup.resetSpinner();
        strength.setText(null);
        quantity.getEditableText().clear();
        familyMember.setText(null);
        physician.setText(null);
        pharmacy.getEditableText().clear();
        prescriptionNumber.getEditableText().clear();
        note.getEditableText().clear();
        prescLabelImage.setImageBitmap(ImageUtility.decodeSampledBitmapFromResource(getApplication().getResources(),
                R.drawable.ic_contact_picture, 96, 96));
        medImage.setImageBitmap(ImageUtility.decodeSampledBitmapFromResource(getApplication().getResources(),
                R.drawable.ic_contact_picture,96, 96));
        medicationBrandName.getEditableText().clear();
        medicationMfg.getEditableText().clear();
        medicationTakeInstr.getEditableText().clear();

    }

    /**
     * processMedication
     * This method processes the medication data collected and inserts or updates the database
     * depending on mode
     * @param mode int
     * @param medication Medication
     * @return rowId of operation
     */
    private long processMedication(int mode, Medication medication ) {

        long rowId;

        if ( mode == Constants.INSERT ) {
            rowId = Database.medTable.insert(medication);
        }
        else {
            rowId = Database.medTable.update(medication);
        }

        return rowId;
    }

    /**
     * setRouteSpinnerSelection
     * This method gets the current route that is in the current medication
     * and sets the spinner position to that selection.
     */
    private void setRouteSpinnerSelection() {
        if ( !StringUtil.isNullOrEmpty(medication.getRoute())){
            routeSpinnerSetup.setSelection(medication.getRoute());
        }
    }

    /**
     * setFormSpinnerSelection
     * This method gets the current route that is in the current medication
     * and sets the spinner position to that selection.
     */
    private void setFormSpinnerSelection() {
        if (!StringUtil.isNullEmptyBlank(medication.getForm())) {
            formSpinnerSetup.setSelection(medication.getForm());
        }
    }

    /**
     * prepareCurrentDosageIntent
     * This method is used to create the intent used to invoke the dosage activity
     * If it is a medication update then the current value of the dosage is extracted
     * and parsed into strenght and unit to be displayed on the dosage screen
     * On insert no extra data is sent
     * @return Intent to be used for Dosage activity
     */
    private Intent prepareCurrentDosageIntent() {
        Intent intent = new Intent(getApplicationContext(), MedictionDosageActivity.class);

        if (mode == Constants.UPDATE && StringUtil.isNotNullEmptyBlank(strength.getText().toString())) {
            String[] tokens = StringUtil.parseString(strength.getText().toString(), "[ ]");
            intent.putExtra(Constants.DOSAGE_STRENGTH_VALUE, tokens[Constants.STRENGTH]);
            intent.putExtra(Constants.DOSAGE_UNIT_VALUE, tokens[Constants.UNIT]);
        }

        return intent;
    }

    /**
     * prepareFamilyMemberIntent
     * This method prepares the intent to be used to call the family member list activity
     * @return Intent
     */
    private Intent prepareFamilyMemberIntent() {
        Intent intent = new Intent(getApplicationContext(), FamilyMemberListActivity.class);
        intent.putExtra(Constants.MODE, Constants.NAME_REQUESTED);
        return intent;
    }

    /**
     * setPhysicianInternt
     * This method prepares the intent for the Physcisian list activity
     * @return Intent
     */
    private Intent preparePhysicianIntent() {
        Intent intent =new Intent(getApplicationContext(), PhysicianListActivity.class);
        intent.putExtra(Constants.MODE, Constants.NAME_REQUESTED);
        return intent;
    }
}
