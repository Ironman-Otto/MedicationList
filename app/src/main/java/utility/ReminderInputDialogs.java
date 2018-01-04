package utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.lsdinfotech.medicationlist.R;
import com.lsdinfotech.medicationlist.ReminderActivity;
import com.lsdinfotech.medicationlist.SpecificReminderActivity;

/**
 * Created by Ironman on 5/8/2017.
 * @author Otto L. Lecuona
 * The purpose of this class is to provide dialogs for inputing parameters
 * of reminders
 */

public class ReminderInputDialogs {

    private static final String FREQ = "freq";
    private static final String INTERVAL = "interval";
    private Activity activity;
    private TextView textView;
    private SpinnerUtil intervalList;
    private Context context;
    private TextView interval;
    private TextView count;
    private SpinnerUtil countList;
    private int frequency;
    private ViewGroup nullParent = null;

    private static final boolean NOSORT = false;

    public ReminderInputDialogs(Activity activity) {
        this.activity = activity;
    }

    public ReminderInputDialogs(Activity activity, TextView textView) {
        this.activity = activity;
        this.textView = textView;
    }

    public ReminderInputDialogs(Activity activity, Context context, TextView interval, TextView count, int frequency){
        this.activity = activity;
        this.context = context;
        this.interval = interval;
        this.count = count;
        this.frequency = frequency;
    }

    /**
     * getDaysOfWeek
     * This method returns the list of days for the occurrence
     * The chosen days are processed and set in the textview
     * specified at construction
     * @return AlertDialog presenting days of week
     */
    public AlertDialog getDaysOfWeek() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.days_of_week_dialog, nullParent);
        builder.setView(layout);
        builder.setTitle("Pick day for reminder");
        final ViewHolder viewHolder = new ViewHolder(layout);
        builder.setPositiveButton("save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textView.setText(processDaysChecked(viewHolder));
            }
        });



        return builder.create();
    }

    /**
     * ViewHolder
     * This class is used to contain the days of the week used
     * in reminders day to occur input
     */
    private class ViewHolder {
        CheckBox sunday;
        CheckBox monday;
        CheckBox tuesday;
        CheckBox wednesday;
        CheckBox thursday;
        CheckBox friday;
        CheckBox saturday;

        ViewHolder(View layout) {
            sunday = (CheckBox)layout.findViewById(R.id.day_sunday);
            monday = (CheckBox)layout.findViewById(R.id.day_monday);
            tuesday = (CheckBox)layout.findViewById(R.id.day_tuesday);
            wednesday = (CheckBox)layout.findViewById(R.id.day_wednesday);
            thursday = (CheckBox)layout.findViewById(R.id.day_thursday);
            friday = (CheckBox)layout.findViewById(R.id.day_friday);
            saturday = (CheckBox)layout.findViewById(R.id.day_saturday);
        }
    }

    /**
     * processDaysChecked
     * This method builds a string with the days that have
     * been checked for the reminder. It is used to eventually
     * build the BYDAY component of the RRULE, recurrcence rule
     * @param viewHolder container of checkboxes
     * @return String days of week checked
     */
    private String processDaysChecked(ViewHolder viewHolder) {
        String days = "";
        if (viewHolder.sunday.isChecked()) {
            days = "Sunday";
        }
        if (viewHolder.monday.isChecked()){
            days = days + " Monday";
        }
        if(viewHolder.tuesday.isChecked()){
            days = days + " Tuesday";
        }
        if(viewHolder.wednesday.isChecked()){
            days = days + " Wednesday";
        }
        if(viewHolder.thursday.isChecked()){
            days =  days + " Thursday";
        }
        if(viewHolder.friday.isChecked()) {
            days = days + " Friday";
        }
        if(viewHolder.saturday.isChecked()) {
            days = days + " Saturday";
        }

        days = days.trim();
        days = days.replace(" ", ", ");

        return days;
    }

    /**
     * selectReminderType
     * This method puts up a dialog to get the type of reminder
     * @return AlertDialog with list of reminder types
     */
    public AlertDialog selectReminderType(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.reminder_type_list, nullParent);
        final RadioGroup radioGroup = (RadioGroup) layout.findViewById(R.id.reminder_type_list_grp);

        builder.setTitle("Reminder")
                .setView(layout)
                .setPositiveButton("Select", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int selectId = radioGroup.getCheckedRadioButtonId();
                        activity.startActivity(setReminderTypeIntent(selectId));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }

    /**
     * setReminderTypeIntent
     * This method creates an intent to start the appropriate activity based
     * on the user's choice from the dialog
     * @param id radio button checked
     * @return Intent to correct activity
     */
    private Intent setReminderTypeIntent(int id) {

        Intent intent = new Intent(activity.getApplicationContext(), ReminderActivity.class);
        switch (id){
            case R.id.reminder_type_daily:{
                intent.putExtra(FREQ, "DAILY");
                intent.putExtra(INTERVAL, SpinnerConstants.DAILY);
                break;
            }
            case R.id.reminder_type_weekly:{
                intent.putExtra(FREQ, "WEEKLY");
                intent.putExtra(INTERVAL, SpinnerConstants.WEEKLY);
                break;
            }
            case R.id.reminder_type_monthly:{
                intent.putExtra(FREQ, "MONTHLY");
                intent.putExtra(INTERVAL, SpinnerConstants.MONTHLY);
                break;
            }
            case R.id.reminder_type_yearly:{
                intent.putExtra(FREQ, "YEARLY");
                intent.putExtra(INTERVAL, SpinnerConstants.WEEKLY);
                break;
            }
            case R.id.reminder_type_specific_date:{
                return new Intent(activity.getApplicationContext(), SpecificReminderActivity.class);
            }
        }

        return intent;
    }

    /**
     * getOccurenceInterval
     * This method presents a spinner based on the repeat frequency specified
     * It read the spinner value when the save button is clicked
     * @return AlertDialog
     */
    public AlertDialog getOccurrenceInterval() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_occurs_every, nullParent);
        setIntervalSpinner(layout, selectIntervalList(frequency) );

        builder.setTitle(R.string.reminder_input_repeat);
        builder.setView(layout);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                interval.setText(intervalList.getSelection());
            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }

    /**
     * setIntervalSpinner
     * This method creates the spinner used to set the reminder interval
     * The interval will be used to build the RRULE, recurrence rule
     * @param layout view containing the spinner
     * @param list a string array of the proper intervals
     */
    private void setIntervalSpinner(View layout, String[] list) {
        Spinner intervalSpinner = (Spinner)layout.findViewById(R.id.dialog_occurs_spinner);
        intervalList = new SpinnerUtil(context, intervalSpinner, list,
                R.layout.my_spinner_item, R.layout.my_spinner_dropdown_item);
        intervalList.setUpSpinner(NOSORT);

        String intervalValue = this.interval.getText().toString();
        if (StringUtil.isNotNullEmptyBlank(intervalValue)){
            intervalList.setSelection(intervalValue);
        }
    }

    /**
     * selectIntervalList
     * @param frequency of Reminder (occurrence)
     * @return string array containing the list of intervals
     */
    private String[] selectIntervalList(int frequency) {
        switch (frequency) {
            case SpinnerConstants.HOURLY:{
                return SpinnerConstants.spinnerHours;
            }
            case SpinnerConstants.DAILY:{
                return SpinnerConstants.spinnerDays;
            }
            case SpinnerConstants.WEEKLY:{
                return SpinnerConstants.spinnerWeeks;
            }
            case SpinnerConstants.MONTHLY:{
                return SpinnerConstants.spinnerMonths;
            }
            case SpinnerConstants.YEARLY:{
                return SpinnerConstants.spinnerYears;
            }
        }
        return null;
    }


    /**
     * setCountSpinner
     * This method creates an instance of SpinnerUtil for the
     * count spinner. It also sets the spinner to what ever value
     * it contains on the ReminderActivity screen
     * @param layout lsyout containing spinner
     * @param list the list of items for the spinner
     */
    private void setCountSpinner(View layout, String[] list) {
        Spinner intervalSpinner = (Spinner)layout.findViewById(R.id.dialog_occurs_spinner);
        countList = new SpinnerUtil(context, intervalSpinner, list,
                R.layout.my_spinner_item, R.layout.my_spinner_dropdown_item);
        countList.setUpSpinner(NOSORT);

        String countValue = this.count.getText().toString();
        if (StringUtil.isNotNullEmptyBlank(countValue)){
            countList.setSelection(countValue);
        }
    }

    public AlertDialog getOccurrenceCount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_occurs_every, nullParent);
        setCountSpinner(layout, SpinnerConstants.spinnerCount );

        builder.setTitle(R.string.reminder_input_repeat);
        builder.setView(layout);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                count.setText(countList.getSelection());
            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }

    public AlertDialog getUntil() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.reminder_input_until)
                .setMessage(R.string.reminder_input_until_msg)
                .setPositiveButton("DATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TimeDatePickers timeDatePickers = new TimeDatePickers(activity, "MM-dd-yyyy", "");
                        timeDatePickers.showDatePicker(textView);
                    }
                })
                .setNegativeButton("CLEAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        textView.setText("");
                    }
                });
        return builder.create();

    }
}
