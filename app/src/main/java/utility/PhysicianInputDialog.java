package utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.lsdinfotech.medicationlist.R;

/**
 * Created by Ironman on 5/2/2017.
 * @author Otto L. Lecuona
 * This class is used to handle the inputs for the physician activity
 */

public class PhysicianInputDialog {
    private TextView        textView;
    private Activity        activity;
    private Context         context;
    private SpinnerUtil     stateList;
    private ViewGroup       viewGroup = null;

    private TextView        address1;
    private TextView        address2;
    private TextView        city;
    private TextView        state;
    private TextView        zip;

    /**
     * setAddressViews
     * This method passes in the views neseccary for the address
     * dialog
     * @param addr1 first line  in address
     * @param addr2 second line in address
     * @param city city
     * @param state state
     * @param zip zipCode
     */
    public void setAddressViews(TextView addr1, TextView addr2, TextView city, TextView state, TextView zip) {
        this.address1 = addr1;
        this.address2 = addr2;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }

    /**
     * PhysicianInputDialog
     * Constructor
     * @param textView TextView to be updated with input
     * @param activity Activity owning dialog
     * @param context Context of activity
     */
    public PhysicianInputDialog(TextView textView, Activity activity, Context context) {
        this.textView = textView;
        this.activity = activity;
        this.context = context;
    }

    /**
     * getSpecialty
     * This method is used to get the physician specialty.
     * It creates AlertDialog with custom view.
     * @return AlertDialog to be shown
     */
    public AlertDialog getSpecialty() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_specialty_details, viewGroup);
        final EditText inputSpecialty = (EditText)layout.findViewById(R.id.specialty_dialog_edittext);
        inputSpecialty.setText(readCurrentSpecialty());

        builder.setView(layout);
        builder.setTitle(R.string.enter_specialty);
        builder.setPositiveButton(R.string.dialog_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textView.setText(StringUtil.capFirstCharString(inputSpecialty.getText().toString().trim()));
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
     * getPhone
     * This method is used to get the phone number for the physician
     * @return AlertDialog used to get the phone number
     */
    public AlertDialog getPhone() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_phone_details, viewGroup);
        final EditText inputPhone = (EditText) layout.findViewById(R.id.phone_dialog_edittext);
        inputPhone.setText(PhoneUtil.unFormatPhoneNumber(readCurrentTextView()));

        builder.setView(layout);
        builder.setTitle(R.string.enter_phone_number);
        builder.setPositiveButton(R.string.dialog_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textView.setText(PhoneUtil.formatPhoneNumber(activity, inputPhone.getText().toString()));
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
     * getFax
     * This method creates a dialog to get the fax number
     * @return AlertDialog to get fax
     */
    public AlertDialog getFax() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_fax_details, viewGroup);
        final EditText inputFax = (EditText) layout.findViewById(R.id.fax_number_dialog_edittext);
        inputFax.setText(PhoneUtil.unFormatPhoneNumber(readCurrentTextView()));

        builder.setView(layout);
        builder.setTitle(R.string.enter_fax_number);
        builder.setPositiveButton(R.string.dialog_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textView.setText(PhoneUtil.formatPhoneNumber(activity, inputFax.getText().toString()));
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
     * getEmail
     * This method is used to create a dialog for email
     * @return AlertDialog for input
     */
    public AlertDialog getEmail() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_email_details, viewGroup);
        final EditText inputEmail = (EditText) layout.findViewById(R.id.email_dialog_edittext);
        inputEmail.setText(readCurrentTextView());

        builder.setView(layout);
        builder.setTitle(R.string.enter_email);
        builder.setPositiveButton(R.string.dialog_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textView.setText(inputEmail.getText().toString());
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
     * readCurrentSpecialty
     * This method returns the value of the physician specialty
     * from the physician activity. The specialty is in the activity
     * from the database. the return value is used to prepopulate
     * the dialog layout
     * @return String with value of current specialty
     */
    private String readCurrentSpecialty() {
        if (StringUtil.isNotNullEmptyBlank(textView.getText().toString())) {
            return textView.getText().toString();
        }else {
            return null;
        }
    }

    /**
     * readCurrentTextView
     * This method returns the value of the current phone number. It is used
     * to prepopulate the input dialog
     * @return String with phone number
     */
    private String readCurrentTextView() {
        if (StringUtil.isNotNullEmptyBlank(textView.getText().toString())){
            return textView.getText().toString();
        } else {
            return "";
        }
    }

    /**
     * getAddress
     * This method puts up the dialog to get the address for the physician
     * @return AlertDialog
     */
    public AlertDialog getAddress() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_address_details, viewGroup);

        final EditText addr1 = (EditText)layout.findViewById(R.id.address1_dialog);
        final EditText addr2 = (EditText)layout.findViewById(R.id.address2_dialog);
        final EditText city = (EditText)layout.findViewById(R.id.city_dialog);
        final EditText zip = (EditText)layout.findViewById(R.id.zip_dialog);
        setUpStateSpinner(layout);
        extractAddress(addr1, addr2, city, zip);

        builder.setView(layout);
        builder.setTitle("Enter address");
        builder.setPositiveButton(R.string.dialog_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    setAddress(addr1, addr2, city, zip);

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
     * setUpStateSpinner
     * This method sets up the spinner with all the states
     * @param layout containing the details of the dialog
     */
    private void setUpStateSpinner(View layout) {
        Spinner state = (Spinner)layout.findViewById(R.id.state_dialog);
        stateList = new SpinnerUtil(context, state, SpinnerConstants.stateList,
                R.layout.my_spinner_item, R.layout.my_spinner_dropdown_item);
        stateList.setUpSpinner(SpinnerConstants.NOSORT);

        String stateValue = this.state.getText().toString();
        if (StringUtil.isNotNullEmptyBlank(stateValue)){
            stateList.setSelection(stateValue);
        }
    }

    /**
     * setAddress
     * This method is used to read the address that was
     * input in the dialog
     * @param addr1 address line 1
     * @param addr2 address line 2
     * @param city city
     * @param zip zipcode
     */
    private void setAddress(EditText addr1, EditText addr2, EditText city, EditText zip) {

        this.address1.setText(addr1.getText().toString());
        this.address2.setText(addr2.getText().toString());
        this.city.setText(city.getText().toString());
        this.zip.setText(zip.getText().toString());

        if (stateList.getSelection().equals("Clear")){
            this.state.setText("");
        } else {
            this.state.setText(stateList.getSelection());
        }

    }

    /**
     * extractAddress
     * This method extracts the address from the physician screen
     * and populates the address in the dialog
     * @param addr1 address line 1
     * @param addr2 address line 2
     * @param city city
     * @param zip zip code
     */
    private void extractAddress(EditText addr1, EditText addr2, EditText city, EditText zip) {
        addr1.setText(this.address1.getText().toString());
        addr2.setText(this.address2.getText().toString());
        city.setText(this.city.getText().toString());
        zip.setText(this.zip.getText().toString());
    }
}
