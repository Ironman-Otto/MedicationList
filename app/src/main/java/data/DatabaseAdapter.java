package data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ironman on 4/11/2017.
 * Database adapter for the Medication List application
 */

public class DatabaseAdapter {

    private static final String     DATABASE_NAME = "medicationList.db";
    private static final int        DATABASE_VERSION = 1;

    private DatabaseHelper          mDbHelper;
    private SQLiteDatabase          mDb;
    private final Context           mCtx;

    /**
     * DatabaseAdapter
     *
     * Constructor saves the context to be used with DatabaseHelper
     * @param context Context
     */
    public DatabaseAdapter(Context context) {
        mCtx = context;
    }

    /**
     * open
     *
     * Open the database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this DatabaseAdapter
     * @throws SQLException signal error
     */
    public DatabaseAdapter open() throws SQLException {
        mDbHelper   = new DatabaseHelper(mCtx);
        mDb         = mDbHelper.getWritableDatabase();
        new Database(mDb);
        return this;
    }

    /**
     * close
     *
     * Close database
     */
    public void close(){
        mDbHelper.close();
    }

    /**
     * getDatabase
     *
     * Get a copy of the database
     * @return mDb SQLiteDatabase
     */
    public SQLiteDatabase getDatabase() {
        return mDb;
    }

    /**
     * DatabaseHelper
     *
     * Database helper class. Used to create and upgrade the database.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper( Context context ){
            super( context, DATABASE_NAME, null, DATABASE_VERSION );
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(MedsDb.CREATE_MEDICATION_TABLE);
            db.execSQL(FamilyMemberDb.CREATE_TABLE_FAMILY_MEMBER);
            db.execSQL(PhysicianDb.CREATE_TABLE_PHYSICIAN);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        }
    }
}
