package utility;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import java.util.TimeZone;

import data.Constants;
import model.MyEvent;

/**
 * Created by Ironman on 5/5/2017.
 * @author Otto L. Lecuona
 * This class is used to manage Events and Reminders
 */

public class EventReminder {

    private Uri         EVENT_URI = CalendarContract.Events.CONTENT_URI;
    private Uri         REMINDER_URI = CalendarContract.Reminders.CONTENT_URI;
    private long        calendarId;
    private String      accountName;
    private Context     context;
    private int         mode;
    private long        eventId;

    public EventReminder(Context context, long calendarId, String calendarAccountName) {
        this.calendarId = calendarId;
        this.accountName = calendarAccountName;
        this.context = context;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    /**
     * getTimeZoneIDs
     * This method retrieves all the available time zones
     * @return String[] containing TimeZones
     */
    public String[] getTimeZoneIDs() {

        return TimeZone.getAvailableIDs();
    }

    /**
     * getDefaultTimeZoneID
     * This method returns the system default time zone
     * @return String with the time zone
     */
    public String getDefaultTimeZoneID() {

        return TimeZone.getDefault().getID();
    }


    /**
     * buildEventUri
     * This method builds the Uri for the event using the specified account name
     * @return Uri of the event
     */
    private Uri buildEventUri() {
        return EVENT_URI
                .buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE,
                        CalendarContract.ACCOUNT_TYPE_LOCAL)
                .build();
    }

    /**
     * buildReminderUri
     * This method builds a sync adapter and Uri for the
     * reminder table based on current calender account
     * @return Uri for reminder table
     */
    private Uri buildReminderUri() {
        return REMINDER_URI
                .buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE,
                        CalendarContract.ACCOUNT_TYPE_LOCAL)
                .build();
    }
    /**
     * addEventNonRecurring
     * This method adds a non recuring event to the calender
     * @param title event title to display
     * @param description event description
     * @param dtstart start of event in milliseconds
     * @param dtend end of event in milleseconds
     * @return long event id
     */
    public long addEventNonRecurring(String title, String description, long dtstart, long dtend, String timeZone) {
        long eventIdNew;
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues cv = new ContentValues();

        cv.put(CalendarContract.Events.CALENDAR_ID, calendarId);
        cv.put(CalendarContract.Events.TITLE, title);
        cv.put(CalendarContract.Events.DESCRIPTION, description);
        cv.put(CalendarContract.Events.DTSTART, dtstart);
        cv.put(CalendarContract.Events.DTEND, dtend);
        cv.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone);

        if (mode != Constants.UPDATE) {
            Uri returnUri = contentResolver.insert(buildEventUri(), cv);
            if ( returnUri != null) {
                eventIdNew = Long.parseLong(returnUri.getLastPathSegment());
                return eventIdNew;
            }
        } else {
            updateEvent(cv, eventId);
        }
        return eventId;
    }

    /**
     * addEventRecurring
     * This method is used to create a recurring event in the current calendar
     * @param title of event
     * @param description of event
     * @param dtstart start date in milliseconds
     * @param rRule recurrsion rule
     * @param timeZone time zone
     * @return long event Id
     */
    public long addEventRecurring(String title, String description, long dtstart, String rRule, String timeZone) {
        long eventIdNew;
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues cv = new ContentValues();

        cv.put(CalendarContract.Events.CALENDAR_ID, calendarId);
        cv.put(CalendarContract.Events.TITLE, title);
        cv.put(CalendarContract.Events.DESCRIPTION, description);
        cv.put(CalendarContract.Events.DTSTART, dtstart);
        cv.put(CalendarContract.Events.RRULE, rRule);
        cv.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone);

        if (mode != Constants.UPDATE) {
            Uri returnUri = contentResolver.insert(buildEventUri(), cv);
            if ( returnUri != null) {
                eventIdNew = Long.parseLong(returnUri.getLastPathSegment());
                return eventIdNew;
            }
        } else {
            updateEvent(cv, eventId);
        }
        return eventId;
    }



    /**
     * deleteEvent
     * This method deletes a given event from the calendar
     * @param context application context
     * @param eventID id of the event to delete
     */
    public void deleteEvent(Context context, long eventID) {

        Uri deleteEventUri = ContentUris.withAppendedId(buildEventUri(),eventID);
        context.getContentResolver().delete(deleteEventUri,null,null);
    }

    /**
     * updateEvent
     * This method is used to update an existing event.
     * @param eventValues new values for the event
     * @param eventID id identifying the event to update
     */
    private void updateEvent(ContentValues eventValues, long eventID) {

        Uri updateEventUri = ContentUris.withAppendedId(buildEventUri(), eventID);
        context.getContentResolver().update(updateEventUri, eventValues, null, null);

    }

    /**
     * queryEvents
     * This method queries the events in the specific calendar id
     * @return array of events
     */
    public MyEvent[] queryEvents() {
        final String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.RRULE
        };
        final int PROJECTION_ID_INDEX = 0;
        final int PROJECTION_TITLE_INDEX = 1;
        final int PROJECTION_DTSTART_INDEX = 2;
        final int PROJECTION_RRULE_INDEX = 3;
        MyEvent[] myEvents = null;

        String selection = CalendarContract.Events.CALENDAR_ID + " = ?";
        String[] selectionArgs = new String[] {String.valueOf(calendarId)};

        Uri eventUri = buildEventUri();
        Cursor c;
        ContentResolver cr = context.getContentResolver();
        c = cr.query(eventUri, EVENT_PROJECTION, selection, selectionArgs, null);
        if ( c != null && c.getCount() > 0) {
            c.moveToFirst();
            myEvents = new MyEvent[c.getCount()];

            int i = 0;
            long dtstart;
            String rrule;
            long eventId;
            String title;

            do {
                eventId = c.getLong(c.getColumnIndex(EVENT_PROJECTION[PROJECTION_ID_INDEX]));
                title = c.getString(c.getColumnIndex(EVENT_PROJECTION[PROJECTION_TITLE_INDEX]));
                dtstart = c.getLong(c.getColumnIndex(EVENT_PROJECTION[PROJECTION_DTSTART_INDEX]));
                rrule = c.getString(c.getColumnIndex(EVENT_PROJECTION[PROJECTION_RRULE_INDEX]));
                myEvents[i] = new MyEvent(eventId, title, dtstart,rrule );
                i++;

            }while (c.moveToNext());

            c.close();
        }


        return myEvents;

    }

    /**
     * addReminder
     * This method sets a reminder for the given event
     * @param eventID identifier for event
     * @param minutes minutes before event to trigger reminder
     * @param method type of method used to display reminder
     * @return long id of created reminder;
     */
    public long addReminder(long eventID, int minutes, int method ) {

        long reminderId = 0;

        ContentValues reminderValues = new ContentValues();

        // Set reminder on Event ID.
        reminderValues.put(CalendarContract.Reminders.EVENT_ID, eventID);
        reminderValues.put(CalendarContract.Reminders.MINUTES, minutes);
        reminderValues.put(CalendarContract.Reminders.METHOD, method);

        int res = context.checkCallingOrSelfPermission(Manifest.permission.WRITE_CALENDAR);
        if (res == PackageManager.PERMISSION_GRANTED) {

            Uri reminderUri = context.getContentResolver().insert(buildReminderUri(), reminderValues);
            try {
                reminderId = Long.parseLong(reminderUri.getLastPathSegment());
            }catch (NullPointerException npe){
                return 0;
            }
        }

        return reminderId;
    }

    /**
     * deleteReminder
     * This method deletes the specified reminder. It does not
     * delete the associated event
     * @param reminderID identifier for remindere
     */
    public void deleteReminder(Long reminderID){

        Uri reminderUri = ContentUris.withAppendedId(CalendarContract.Reminders.CONTENT_URI,reminderID);
        context.getContentResolver().delete(reminderUri, null, null);
    }

    /**
     * updateReminder
     * This method will update the specified reminder with the new values
     * @param reminderValues values to update
     * @param reminderID identifier of reminder to update
     */
    public void updateReminder(ContentValues reminderValues, long reminderID) {

        Uri updateReminderUri = ContentUris.withAppendedId(CalendarContract.Reminders.CONTENT_URI,reminderID);
        context.getContentResolver().update(updateReminderUri, reminderValues, null, null);
    }
}