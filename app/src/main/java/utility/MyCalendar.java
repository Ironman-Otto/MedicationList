package utility;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

/**
 * Created by Ironman on 5/18/2017.
 * @author Otto L. Lecuona
 * This class is used to create calendars
 */

public class MyCalendar {
    private final Uri CAL_URI = CalendarContract.Calendars.CONTENT_URI;

    /**
     * buildCalendarUri
     * This method is used to create a URI for the calendar it is used
     * as a syncadapter to allow for more access to calendar properties
     * @return Uri of the calendar
     */
    private Uri buildCalendarUri(String accountName) {
        return CAL_URI
                .buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE,
                        CalendarContract.ACCOUNT_TYPE_LOCAL)
                .build();

    }

    /**
     * createCalendar
     * This method creates a calendar for the user and puts it in the CalendarProvider calendar table
     * @param context application context
     * @param accountName name for calendar account
     * @param calendarName name of calendar
     * @param visibility true for visible calendar, false for invisible
     * @return long calendar id for created calendar
     */
    public long createCalendar(Context context, String accountName, String calendarName, String accountOwner, int visibility) {
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues cv = buildNewCalContentValues(accountName, calendarName, accountOwner, visibility);
        Uri calendarUri = buildCalendarUri(accountName);
        Uri returnUri = contentResolver.insert(calendarUri, cv);
        long calId = 0;
        if (returnUri != null) {
            calId = Long.parseLong(returnUri.getLastPathSegment());
        }

        return  calId;
    }

    /**
     * buildNewCalContentValues
     * This method will build the content values used to create a new calendar
     * @param accountName name of the account
     * @param calendarName name of the calendar
     * @param visibility true for visible calender, false for invisible
     * @return ContentValues to use in create method
     */
    private ContentValues buildNewCalContentValues(String accountName, String calendarName, String accountOwner, int visibility) {

        final ContentValues cv = new ContentValues();
        cv.put(CalendarContract.Calendars.ACCOUNT_NAME, accountName);
        cv.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        cv.put(CalendarContract.Calendars.NAME, calendarName);
        cv.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, calendarName);
        cv.put(CalendarContract.Calendars.CALENDAR_COLOR, 0xEA8561);
        //user can only read the calendar
        cv.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        cv.put(CalendarContract.Calendars.OWNER_ACCOUNT, accountOwner);
        cv.put(CalendarContract.Calendars.VISIBLE, visibility);
        cv.put(CalendarContract.Calendars.SYNC_EVENTS, 1);

        return cv;
    }


    /**
     * deleteCalendar
     * This method deletes the calendar with the specified account name and calendar id
     * @param context application context
     * @param accountName name of calendar acccount
     * @param calendarId id of the calendar
     */
    public void deleteCalendar(Context context, String accountName, long calendarId) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(buildCalendarUri(accountName), calendarId);
        contentResolver.delete(uri, null, null);
    }

    /**
     * InstalledCalendar
     * This class is used to hold information about installed calendars
     */
    public class InstalledCalendar {
        private String calendarName;
        private int calId;
        private String accountName;
        private String ownerAccount;
        private int visibility;

        public InstalledCalendar(){}

        public InstalledCalendar(String calendarName, int calId, String accountName, String ownerAccount) {
            this.calendarName = calendarName;
            this.calId = calId;
            this.accountName = accountName;
            this.ownerAccount = ownerAccount;
        }

        public String getCalendarName() {
            return calendarName;
        }

        public void setCalendarName(String calendarName) {
            this.calendarName = calendarName;
        }

        public int getCalId() {
            return calId;
        }

        public void setCalId(int calId) {
            this.calId = calId;
        }

        public String getAccountName() {
            return accountName;
        }

        public void setAccountName(String accountName) {
            this.accountName = accountName;
        }

        public String getOwnerAccount() {
            return ownerAccount;
        }

        public void setOwnerAccount(String ownerAccount) {
            this.ownerAccount = ownerAccount;
        }

        public int getVisibility() {
            return visibility;
        }

        public void setVisibility(int visibility) {
            this.visibility = visibility;
        }
    }

    /**
     * getCalendar
     * This method reads the Calendar table
     * @param context application context
     * @return InstalledCalendar[] contains set of all installed calendars
     */
    public InstalledCalendar[] getCalendar(Context context) {
        InstalledCalendar[] installedCalendars = null;

        String projection[] = {
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.OWNER_ACCOUNT};

        final int PROJ_ID_INDEX = 0;
        final int PROJ_CAL_DISP_NAME_INDEX = 1;
        final int PROJ_ACCOUNT_NAME_INDEX = 2;
        final int PROJ_OWNER_ACCOUNT_INDEX = 3;

        Uri calendars;
        calendars = Uri.parse("content://com.android.calendar/calendars");

        ContentResolver contentResolver = context.getContentResolver();
        Cursor c = contentResolver.query(calendars, projection, null, null, null);

        if (c != null && c.moveToFirst()){
            installedCalendars = new InstalledCalendar[c.getCount()];
            String calName;
            int calID;
            String accountName;
            String accountOwner;
            int cont= 0;
            do {
                calName = c.getString(c.getColumnIndex(projection[PROJ_CAL_DISP_NAME_INDEX]));
                calID = c.getInt(c.getColumnIndex(projection[PROJ_ID_INDEX]));
                accountName = c.getString(c.getColumnIndex(projection[PROJ_ACCOUNT_NAME_INDEX]));
                accountOwner = c.getString(c.getColumnIndex(projection[PROJ_OWNER_ACCOUNT_INDEX]));
                installedCalendars[cont] = new InstalledCalendar(calName, calID, accountName, accountOwner);
                cont++;
            } while(c.moveToNext());
            c.close();
        }
        return installedCalendars;

    }

    /**
     * getCalendar
     * This method gets the calendar settings for a specific calendar id
     * @param context application context
     * @param calendarID id of calendar to query
     * @return InstalledCalendar contains information about calendar
     */
    public InstalledCalendar getCalendar(Context context, int calendarID) {

        InstalledCalendar calendar = new InstalledCalendar();

        String projection[] = {
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.OWNER_ACCOUNT,
                CalendarContract.Calendars.VISIBLE};

        final int PROJ_ID_INDEX = 0;
        final int PROJ_CAL_DISP_NAME_INDEX = 1;
        final int PROJ_ACCOUNT_NAME_INDEX = 2;
        final int PROJ_OWNER_ACCOUNT_INDEX = 3;
        final int PROJ_VISIBLE_INDEX = 4;

        Uri calendars;
        calendars = Uri.parse("content://com.android.calendar/calendars");

        ContentResolver contentResolver = context.getContentResolver();
        String selection = CalendarContract.Calendars._ID + " = ?";
        String[] selectionArgs = {String.valueOf(calendarID)};
        Cursor c = contentResolver.query(calendars, projection, selection, selectionArgs, null);

        if (c != null && c.getCount() == 1) {
            c.moveToFirst();
            calendar.setCalId(c.getInt(c.getColumnIndex(projection[PROJ_ID_INDEX])));
            calendar.setAccountName(c.getString(c.getColumnIndex(projection[PROJ_ACCOUNT_NAME_INDEX])));
            calendar.setCalendarName(c.getString(c.getColumnIndex(projection[PROJ_CAL_DISP_NAME_INDEX])));
            calendar.setOwnerAccount(c.getString(c.getColumnIndex(projection[PROJ_OWNER_ACCOUNT_INDEX])));
            calendar.setVisibility(c.getInt(c.getColumnIndex(projection[PROJ_VISIBLE_INDEX])));
            c.close();

            return calendar;
        } else {
            return null;
        }

    }

    /**
     * getCalendar
     * @param context application context
     * @param accountName name of calendar to query
     * @return InstalledCalendar for account
     */
    public InstalledCalendar getCalendar(Context context, String accountName) {

        InstalledCalendar calendar = new InstalledCalendar();

        String projection[] = {
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.OWNER_ACCOUNT,
                CalendarContract.Calendars.VISIBLE};

        final int PROJ_ID_INDEX = 0;
        final int PROJ_CAL_DISP_NAME_INDEX = 1;
        final int PROJ_ACCOUNT_NAME_INDEX = 2;
        final int PROJ_OWNER_ACCOUNT_INDEX = 3;
        final int PROJ_VISIBLE_INDEX = 4;

        Uri calendars;
        calendars = Uri.parse("content://com.android.calendar/calendars");

        ContentResolver contentResolver = context.getContentResolver();
        String selection = CalendarContract.Calendars.ACCOUNT_NAME + " = ?";
        String[] selectionArgs = {accountName};
        Cursor c = contentResolver.query(calendars, projection, selection, selectionArgs, null);

        if (c != null && c.getCount() == 1) {
            c.moveToFirst();
            calendar.setCalId(c.getInt(c.getColumnIndex(projection[PROJ_ID_INDEX])));
            calendar.setAccountName(c.getString(c.getColumnIndex(projection[PROJ_ACCOUNT_NAME_INDEX])));
            calendar.setCalendarName(c.getString(c.getColumnIndex(projection[PROJ_CAL_DISP_NAME_INDEX])));
            calendar.setOwnerAccount(c.getString(c.getColumnIndex(projection[PROJ_OWNER_ACCOUNT_INDEX])));
            calendar.setVisibility(c.getInt(c.getColumnIndex(projection[PROJ_VISIBLE_INDEX])));
            c.close();

            return calendar;
        } else {
            return null;
        }

    }

}
