package com.lsdinfotech.medicationlist;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import data.DatabaseAdapter;
import utility.MyCalendar;


/**
 * MedicationListApplication
 * Created by Ironman on 4/11/2017.
 * Singleton used for database
 * Creation of application calendar
 */

public class MedicationListApplication extends Application {

    private DatabaseAdapter dbAdapter;

    @Override
    public void onCreate() {
        super.onCreate();

        dbAdapter = new DatabaseAdapter(getApplicationContext());
        dbAdapter.open();

    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        dbAdapter.close();
    }

    /**
     * getDatabase
     *
     * Returns a copy of the application database
     * @return SQLiteDatabase
     */
    public SQLiteDatabase getDatabase() {
        return dbAdapter.getDatabase();
    }

}
