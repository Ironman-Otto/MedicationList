package data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import model.Physician;

/**
 * Created by Ironman on 4/18/2017.
 * This class handles Physican database table
 */

public class PhysicianDb {

    private SQLiteDatabase mDb;

    static final String DATABASE_TABLE_PHYSICIAN = "tbl_physician";
    private static final String PRI_KEY_PHYSICIAN = "pk_id";
    private static final String PREFIX = "prefix";
    private static final String FIRST_NAME = "first_name";
    private static final String MIDDLE_NAME = "middle_name";
    private static final String LAST_NAME = "last_name";
    private static final String SUFFIX = "suffix";
    private static final String SPECIALTY = "specialty";
    private static final String ADDRESS1 = "address1";
    private static final String ADDRESS2 = "address2";
    private static final String CITY = "city";
    private static final String STATE = "state";
    private static final String ZIP = "zipcode";
    private static final String PHONE = "phoneNumber";
    private static final String FAX = "faxNumber";
    private static final String EMAIL = "email";
    private static final String IMAGE = "image";

    static final String CREATE_TABLE_PHYSICIAN =
            "create table " + DATABASE_TABLE_PHYSICIAN + " ("
                    + PRI_KEY_PHYSICIAN + " integer primary key autoincrement, "
                    + PREFIX + " text, "
                    + FIRST_NAME + " text, "
                    + MIDDLE_NAME + " text, "
                    + LAST_NAME + " text not null, "
                    + SUFFIX + " text, "
                    + SPECIALTY + " text, "
                    + ADDRESS1 + " text, "
                    + ADDRESS2 + " text, "
                    + CITY + " text, "
                    + STATE + " text, "
                    + ZIP + " text, "
                    + PHONE + " text, "
                    + FAX + " text, "
                    + EMAIL + " text, "
                    + IMAGE + " blob "
                    + ");";


    PhysicianDb(SQLiteDatabase mDb) {
        this.mDb = mDb;
    }

    /**
     * loadCv
     * This method loads the ContentValues from a Physician object
     * @param physician Physician
     * @return cv ContentValues
     */
    private ContentValues loadCv(Physician physician) {

        ContentValues cv = new ContentValues();

        cv.put(PREFIX, physician.getPrefix());
        cv.put(FIRST_NAME, physician.getFirstName());
        cv.put(MIDDLE_NAME, physician.getMiddleName());
        cv.put(LAST_NAME, physician.getLastName());
        cv.put(SUFFIX, physician.getSuffix());
        cv.put(ADDRESS1, physician.getAddress1());
        cv.put(ADDRESS2, physician.getAddress2());
        cv.put(CITY, physician.getCity());
        cv.put(STATE, physician.getState());
        cv.put(ZIP, physician.getZipcode());
        cv.put(PHONE, physician.getPhone());
        cv.put(SPECIALTY, physician.getSpecialty());
        cv.put(FAX, physician.getFax());
        cv.put(EMAIL, physician.getEmail());
        
        if (physician.getPhyscianImage() != null) {
            cv.put(IMAGE, convertBitmapByteArray(physician.getPhyscianImage()));

        }
        
        return cv;
    }

    /**
     * convertBitmapByteArray
     * Convert Bitmap to byte array
     * @param bitmap Bitmap
     * @return byte[]
     */
    private byte[] convertBitmapByteArray( Bitmap bitmap ) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress( Bitmap.CompressFormat.PNG, 100, bos );
        return bos.toByteArray();

    }

    /**
     * Insert a new physician into DB
     * @param physician specifics
     *
     * @return rowId
     */
    public int insert(Physician physician) {

        ContentValues cv = loadCv(physician);
        return (int)mDb.insert(DATABASE_TABLE_PHYSICIAN, null, cv);


    }

    /**
     * update
     * Update family member in database
     * @param physician physician object
     * @return rowId long
     */
    public int update( Physician physician ) {

        ContentValues cv = loadCv( physician );
        String whereClause = PRI_KEY_PHYSICIAN + " = " + physician.getPrimaryKey();
        return mDb.update(DATABASE_TABLE_PHYSICIAN, cv, whereClause, null );
    }

    /**
     * query
     * Query the physician table for a specific physician
     * @param rowId int
     * @return Cursor
     */
    private Cursor query(int rowId) {

        String whereClause = PRI_KEY_PHYSICIAN + " = " + rowId;
        return mDb.query(DATABASE_TABLE_PHYSICIAN, null, whereClause, null, null, null, null);

    }

    /**
     * queryAll
     * Query the physician table for all physicians
     * @return Cursor
     */
    private Cursor queryAll() {
        return  mDb.query(DATABASE_TABLE_PHYSICIAN, null, null, null, null, null, null);

    }

    /**
     * queryWhere
     * Query medication table using where clause
     * @param whereClause what to delete
     * @return Cursor with values
     */
    private Cursor queryWhere(String whereClause) {
        return mDb.query(DATABASE_TABLE_PHYSICIAN, null, whereClause, null, null, null, null);
    }

    /**
     * delete
     * This method deletes a specific row in database family member table
     * @param rowid tp be deleted
     * @return int
     */
    public int delete(int rowid) {
        String whereClause = PRI_KEY_PHYSICIAN + " = " + rowid;
        return mDb.delete(DATABASE_TABLE_PHYSICIAN, whereClause, null);
    }

    /**
     * queryPhysicians
     * Query the  Physician table. Read all records or only one.
     * @param allRecords boolean
     * @param rowId int
     * @return physicians ArrayList<Physician>
     */
    public ArrayList<Physician> queryPhysicians(boolean allRecords, int rowId, String whereClause ) {

        ArrayList<Physician> physicians = new ArrayList<>();
        Physician physician;
        Cursor c;

        if ( allRecords ) {
            c = queryAll();
        }else if ( rowId > 0){
            c = query(rowId);
        } else {
            c = queryWhere(whereClause);
        }

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            do {
                physician = readPhysicianCursor(c);
                physicians.add(physician);
            }
            while ( c.moveToNext() );
        }

        return physicians;
    }

    /**
     * readPhysicianCursor
     * this method reads the current cursor and populates a Physician structure
     * @param c Cursor
     * @return Physician
     */
    private Physician readPhysicianCursor(Cursor c) {

        Physician physician = new Physician();

        physician.setPrimaryKey(c.getInt(c.getColumnIndex(PRI_KEY_PHYSICIAN)));
        physician.setPrefix(c.getString(c.getColumnIndex(PREFIX)));
        physician.setFirstName(c.getString(c.getColumnIndex(FIRST_NAME)));
        physician.setMiddleName(c.getString(c.getColumnIndex(MIDDLE_NAME)));
        physician.setLastName(c.getString(c.getColumnIndex(LAST_NAME)));
        physician.setSuffix(c.getString(c.getColumnIndex(SUFFIX)));
        physician.setAddress1(c.getString(c.getColumnIndex(ADDRESS1)));
        physician.setAddress2(c.getString(c.getColumnIndex(ADDRESS2)));
        physician.setCity(c.getString(c.getColumnIndex(CITY)));
        physician.setState(c.getString(c.getColumnIndex(STATE)));
        physician.setZipcode(c.getString(c.getColumnIndex(ZIP)));
        physician.setPhone(c.getString(c.getColumnIndex(PHONE)));
        physician.setSpecialty(c.getString(c.getColumnIndex(SPECIALTY)));
        physician.setFax(c.getString(c.getColumnIndex(FAX)));
        physician.setEmail(c.getString(c.getColumnIndex(EMAIL)));

        byte[] img = c.getBlob(c.getColumnIndex(IMAGE));
        if(img != null) {
            physician.setPhyscianImage(BitmapFactory.decodeByteArray(img, 0, img.length));
        }

        return physician;
    }
}
