package utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.lsdinfotech.medicationlist.R;

/**
 * Created by Ironman on 4/30/2017.
 * This class handles getting simple input for activities
 */

public class FamilyInputDialog {
    private TextView textView;
    private Activity activity;
    private Context context;
    private String feetInput;
    private String inchesInput;
    private SpinnerUtil feetSpinner;
    private SpinnerUtil inchesSpinner;
    private SpinnerUtil bloodTypeSpinner;

    /**
     * FamilyInputDialog constructor
     * @param textView textview that is updated by the dialogs
     * @param activity the activity calling the dialogs
     * @param context the context of the application
     */
    public FamilyInputDialog(TextView textView, Activity activity, Context context) {
        this.textView = textView;
        this.activity = activity;
        this.context = context;
    }

    /**
     * getGender
     * This method puts up a dialog to get the gender information
     * for the family member
     * @return AlertDialog
     */
    public AlertDialog getGender() {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.dialog_select_gender);
        builder.setPositiveButton(R.string.dialog_male, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textView.setText(R.string.dialog_male);
            }
        });
        builder.setNegativeButton(R.string.dialog_female, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textView.setText(R.string.dialog_female);
            }
        });

        return builder.create();
    }

    /**
     * getHeight
     * This method is used to get the height of the family member
     * It uses two spinners one for feet and the other for inches.
     * It constructs the output textview in the format
     * x feet y inches
     * @return AlertDialog
     */
    public AlertDialog getHeight() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_height_details, null);
        extractFeetInches();
        setUpFeetSpinner(layout);
        setUpInchesSpinner(layout);

        builder.setTitle(R.string.enter_height);
        builder.setView(layout)
                .setPositiveButton(R.string.dialog_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String height = feetSpinner.getSelection() + " feet " + inchesSpinner.getSelection() + " inches ";
                        textView.setText(height);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }

    /**
     * extractFeetInches
     * This method checks the incoming textview representing the height
     * If it is a valid string it parses the string and breaks out
     * The feet and  inches components. The input string is in the format
     * "x feet y inches"
     */
    private void extractFeetInches() {
        String input = textView.getText().toString();
        if (StringUtil.isNotNullEmptyBlank(input)) {
            String[] tokens = StringUtil.parseString(input, "[ ]");
            if (tokens.length == 4){
                feetInput = tokens[0];
                inchesInput = tokens[2];
            }
        }

    }

    /**
     * setUpFeetSpinner
     * This method sets up the spinner used to input the feet component
     * If there is already a feet value from a previous setting that
     * value is set as the active selection
     * @param layout view containing the spinner
     */
    private void setUpFeetSpinner(View layout) {
        Spinner feet = (Spinner) layout.findViewById(R.id.height_dialog_feet);
        feetSpinner = new SpinnerUtil(context, feet, SpinnerConstants.feetList,
                R.layout.my_spinner_item, R.layout.my_spinner_dropdown_item);
        feetSpinner.setUpSpinner(SpinnerConstants.NOSORT);

        if (StringUtil.isNotNullEmptyBlank(feetInput)) {
            feetSpinner.setSelection(feetInput);
        }
    }

    /**
     * setUpInchesSpinner
     * This method sets up the spinner used to input the inches component
     * If there is already a inches value from a previous setting that
     * value is set as the active selection
     * @param layout view containing the spinner
     */
    private void setUpInchesSpinner(View layout) {
        Spinner inches = (Spinner) layout.findViewById(R.id.height_dialog_inches);
        inchesSpinner = new SpinnerUtil(context, inches, SpinnerConstants.inchList,
                R.layout.my_spinner_item, R.layout.my_spinner_dropdown_item);
        inchesSpinner.setUpSpinner(SpinnerConstants.NOSORT);

        if (StringUtil.isNotNullEmptyBlank(inchesInput)) {
            inchesSpinner.setSelection(inchesInput);
        }

    }

    /**
     * getWeight
     * This method gets the weight input for family member
     * The weight can be either in lbs or kgs
     * @return AlertDialog
     */
    public AlertDialog getWeight() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_weight_details, null);
        final EditText inputWeight = (EditText)layout.findViewById(R.id.weight_dialog_edittext);
        inputWeight.setText(extractWeight());

        builder.setView(layout);
        builder.setTitle(R.string.enter_weight)
                .setPositiveButton(R.string.dialog_pounds, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        textView.setText(prepareWeightInput(inputWeight, activity.getString(R.string.dialog_pounds)));
                    }
                })
                .setNegativeButton(R.string.dialog_kilogram, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       textView.setText(prepareWeightInput(inputWeight, activity.getString(R.string.dialog_kilogram)));
                    }
                })
                .setNeutralButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });


        return builder.create();
    }

    /**
     * prepareWeightInput
     * This method verifies and formats the input. It sets
     * the weight and appends the units
     * @param inputWeight weight entered in the dialog
     * @param unit lbs or kgs
     * @return String with formated weight
     */
    private String prepareWeightInput(EditText inputWeight, String unit) {
        String input = inputWeight.getText().toString();
        if (StringUtil.isNotNullEmptyBlank(input)) {
            input = input + " " + unit;
            inputWeight.setText(input);
        }

        return input;
    }

    /**
     * extractWeight
     * This method gets the current value of the weight and
     * parses it into the weight and unit.
     * @return String containing the value of the weight
     */
    private String extractWeight() {
        String input = textView.getText().toString();
        if (StringUtil.isNotNullEmptyBlank(input)){
            String[] tokens = StringUtil.parseString(input, "[ ]");
            if (tokens.length == 2) {
                input = tokens[0];
            }
        }

        return input;
    }

    public AlertDialog getBloodType() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_bloodtype_details, null);
        setUpBloodTypeSpinner(layout, extractBloodType());

        builder.setTitle("Select blood type");
        builder.setView(layout)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        textView.setText(bloodTypeSpinner.getSelection());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }

    private String extractBloodType() {
        String input = textView.getText().toString();
        if (StringUtil.isNotNullEmptyBlank(input)) {
            return input;
        }

        return null;
    }

    private void setUpBloodTypeSpinner(View layout, String bloodType) {
        Spinner blood = (Spinner) layout.findViewById(R.id.bloodtype_dialog);
        bloodTypeSpinner = new SpinnerUtil(context, blood, SpinnerConstants.bloodType,
                R.layout.my_spinner_item, R.layout.my_spinner_dropdown_item);
        bloodTypeSpinner.setUpSpinner(SpinnerConstants.NOSORT);

        if (StringUtil.isNotNullEmptyBlank(bloodType)) {
            bloodTypeSpinner.setSelection(bloodType);
        }

    }
}
