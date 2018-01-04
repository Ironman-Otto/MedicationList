package utility;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by fonda on 6/12/2016.
 */
public class TimeDatePickers {

    private Calendar calendar;
    private Context context;
    private String timeFormat;
    private String dateFormat;

    public TimeDatePickers(Context context) {
        this.context = context;
        calendar = Calendar.getInstance();
    }

    public TimeDatePickers(Context context, String dateFormat, String timeFormat) {
        this.context = context;
        calendar = Calendar.getInstance();
        this.dateFormat = dateFormat;
        this.timeFormat = timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    /**
     * showTimePicker
     * This method creates a new TimePickerDialog
     * Sets the onTimeSetListener and updates the value of the textview that is passed in
     * shows the timepicker dialog
     * @param tvIn
     */
    public void showTimePicker(TextView tvIn) {

        final TextView tv;
        tv = tvIn;

        TimePickerDialog timePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener(){

            //@Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                updateTimeButtonText(tv);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

        timePicker.show();

    }

    /**
     * updateTimeButtonText
     * This method populates the textview associated with this picker
     * It uses the timeformat passed previously set
     * @param tv TextView
     */
    private void updateTimeButtonText(TextView tv) {
        // Set the time button text based upon the value from the database
        SimpleDateFormat timeFormat = new SimpleDateFormat(this.timeFormat, Locale.US);
        String timeForButton = timeFormat.format(calendar.getTime());
        tv.setText(timeForButton);
    }

    /**
     * showDatePicker
     * This method creates a DatePickerDialog and sets the onDateSetListener
     * Shows the datepicker and sets the value of the date to the textview passesd in
     * @param tvIn TextView
     */
    public void showDatePicker(TextView tvIn) {

        final TextView tv;
        tv = tvIn;

        DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateText(tv);

            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePicker.show();
    }

    /**
     * updateDateText
     * This method sets the value of the selected date to the associated textview
     * @param textView
     */
    private void updateDateText(TextView textView) {
        // Set the date
        SimpleDateFormat dateFormat = new SimpleDateFormat(this.dateFormat, Locale.US);
        String dateForText = dateFormat.format(calendar.getTime());
        textView.setText(dateForText);

    }

}
