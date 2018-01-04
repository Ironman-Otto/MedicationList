package data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import model.Medication;

/**
 * Created by Ironman on 4/10/2017.
 * MedsDB is the database class associated with Medication data.
 * It provides the methods necessay to access the database.
 */


public class MedsDb {

    // Database
    private SQLiteDatabase mDb;

    // Medication Table fields
    static final String DATABASE_TABLE_MEDICATION = "tbl_medication";
    private static final String PRI_KEY_MEDICATION = "pk_id";
    private static final String NAME = "name";
    private static final String ROUTE = "route";
    private static final String FORM = "form";
    private static final String STRENGTH = "strength";
    private static final String QUANTITY = "quantity";
    public static final String PHYSICIAN_NAME = "physician_name";
    public static final String FAMILY_MEMBER_NAME = "family_member_name";
    private static final String PHARMACY = "pharmacy";
    private static final String PRESCRIPTION_NUMBER = "prescritpion_number";
    private static final String NOTES = "notes";
    public static final String FAMILY_MEMBER_PID = "family_member_pid";
    public static final String PHYSICIAN_PID = "physician_pid";
    private static final String MEDICATION_IMAGE = "medication_image";
    private static final String PRESCR_LABEL_IMAGE = "presc_label_image";
    private static final String BRAND_NAME = "brand_name";
    private static final String MFG = "mfg";
    private static final String TAKE_INSTR = "take_instr";

    // Medication creation
    static final String CREATE_MEDICATION_TABLE =
            "create table " + DATABASE_TABLE_MEDICATION + " ("
            + PRI_KEY_MEDICATION + " integer primary key autoincrement,"
            + NAME + " text,"
            + ROUTE + " text,"
            + FORM + " text,"
            + STRENGTH + " text,"
            + QUANTITY + " text,"
            + PHYSICIAN_NAME + " text,"
            + FAMILY_MEMBER_NAME + " text,"
            + PHARMACY + " text,"
            + PRESCRIPTION_NUMBER + " text,"
            + NOTES + " text,"
            + FAMILY_MEMBER_PID + " integer,"
            + PHYSICIAN_PID + " integer,"
            + MEDICATION_IMAGE + " blob,"
            + PRESCR_LABEL_IMAGE + " blob,"
            + BRAND_NAME + " text,"
            + MFG + " text,"
            + TAKE_INSTR + " text"
            + ");";

    /**
     * MedsDb
     * Constructor setting the database to use
     * @param db SQLiteDatabase
     */
    MedsDb(SQLiteDatabase db) {

        this.mDb = db;

    }

    /**
     * loadCV
     * Load the Content Values using Medication
     *
     * @param meds Medication
     * @return ContentValues
     */
    private ContentValues loadCV(Medication meds) {
        ContentValues cv = new ContentValues();

        cv.put(NAME, meds.getName());
        cv.put(ROUTE, meds.getRoute());
        cv.put(FORM, meds.getForm());
        cv.put(STRENGTH, meds.getStrength());
        cv.put(QUANTITY, meds.getQuantity());
        cv.put(PHYSICIAN_NAME, meds.getPhysicianName());
        cv.put(FAMILY_MEMBER_NAME, meds.getFamilyMemberName());
        cv.put(PHARMACY, meds.getPharmacy());
        cv.put(PRESCRIPTION_NUMBER, meds.getPrescriptionNumber());
        cv.put(NOTES, meds.getNote());
        cv.put(FAMILY_MEMBER_PID, meds.getFamilyMemberPid());
        cv.put(PHYSICIAN_PID, meds.getPhysicianPid());
        cv.put(BRAND_NAME, meds.getBrandName());
        cv.put(MFG, meds.getManufacturer());
        cv.put(TAKE_INSTR, meds.getTakeInstruct());

        if ( meds.getMedicationImage() != null ) {
            cv.put(MEDICATION_IMAGE,convertBitmapByteArray(meds.getMedicationImage()));
        }

        if (meds.getPrescrLabelImage() != null) {
            cv.put(PRESCR_LABEL_IMAGE, convertBitmapByteArray(meds.getPrescrLabelImage() ));
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
     * insert
     * Insert Medication into database
     * @param medication Medication object
     * @return rowId int
     */
    public int insert( Medication medication ) {

        ContentValues cv = loadCV( medication );
        return(int) mDb.insert(DATABASE_TABLE_MEDICATION, null, cv );
    }

    /**
     * update
     * Update Medication in database
     * @param medication Medication object
     * @return rowId int
     */
    public int update( Medication medication ) {

        ContentValues cv = loadCV( medication );
        String whereClause = PRI_KEY_MEDICATION + " = " + medication.getPrimaryKey();
        return mDb.update( DATABASE_TABLE_MEDICATION, cv, whereClause, null );
    }

    /**
     * update
     * This method updates the medication record with the content values passed in
     * according to where clause
     * @param cv content values to change
     * @param whereClause what to change
     * @return int result
     */
    public int update(ContentValues cv, String whereClause) {

        return mDb.update(DATABASE_TABLE_MEDICATION, cv, whereClause, null);
    }

    /**
     * query
     * Query the Medication table for a specific medication
     * @param rowId int
     * @return Cursor
     */
    private Cursor query(int rowId) {

        String whereClause = PRI_KEY_MEDICATION + " = " + rowId;
        return mDb.query(DATABASE_TABLE_MEDICATION, null, whereClause, null, null, null, null);

    }

    /**
     * queryAll
     * Query the medicatiom table for all mediciatons
     * @return Cursor
     */
    private Cursor queryAll() {
        return  mDb.query(DATABASE_TABLE_MEDICATION, null, null, null, null, null, null);

    }

    /**
     * queryWhere
     * Query medication table using where clause
     * @param whereClause specify medication
     * @return Cursor of medications
     */
    private Cursor queryWhere(String whereClause) {
        return mDb.query(DATABASE_TABLE_MEDICATION, null, whereClause, null, null, null, null);
    }

    /**
     * delete
     * This methods deletes a specific record from the database
     * @param rowid row to delete
     * @return int
     */
    public int delete(long rowid) {
        String whereClause = PRI_KEY_MEDICATION + " = " + rowid;
        return mDb.delete(DATABASE_TABLE_MEDICATION, whereClause, null);
    }

    /**
     * queryMedications
     * Query the  Medication table. Read all records or only one.
     * @param allRecords boolean
     * @param rowId int
     * @return medications ArrayList<Medication>
     */
    public ArrayList<Medication> queryMedications( boolean allRecords, int rowId, String whereClause ) {

        ArrayList<Medication> medications = new ArrayList<>();
        Medication medication;
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
                medication = readMedicationCursor(c);
                medications.add(medication);
            }
            while ( c.moveToNext() );
        }

        return medications;
    }

    /**
     * readMedicationCursor
     * Read the current Cursor and build the Medication object
     * @param c Cursor
     * @return  Medication
     */
    private Medication readMedicationCursor(Cursor c) {

        Medication medication = new Medication();

        medication.setPrimaryKey(c.getInt(c.getColumnIndex(PRI_KEY_MEDICATION)));
        medication.setName(c.getString(c.getColumnIndex(NAME)));
        medication.setRoute(c.getString(c.getColumnIndex(ROUTE)));
        medication.setForm(c.getString(c.getColumnIndex(FORM)));
        medication.setStrength(c.getString(c.getColumnIndex(STRENGTH)));
        medication.setQuantity(c.getString(c.getColumnIndex(QUANTITY)));
        medication.setPhysicianName(c.getString(c.getColumnIndex(PHYSICIAN_NAME)));
        medication.setFamilyMemberName(c.getString(c.getColumnIndex(FAMILY_MEMBER_NAME)));
        medication.setPharmacy(c.getString(c.getColumnIndex(PHARMACY)));
        medication.setPrescriptionNumber(c.getString(c.getColumnIndex(PRESCRIPTION_NUMBER)));
        medication.setNote(c.getString(c.getColumnIndex(NOTES)));
        medication.setFamilyMemberPid(c.getInt(c.getColumnIndex(FAMILY_MEMBER_PID)));
        medication.setPhysicianPid(c.getInt(c.getColumnIndex(PHYSICIAN_PID)));
        medication.setBrandName(c.getString(c.getColumnIndex(BRAND_NAME)));
        medication.setManufacturer(c.getString(c.getColumnIndex(MFG)));
        medication.setTakeInstruct(c.getString(c.getColumnIndex(TAKE_INSTR)));

        byte[] image = c.getBlob(c.getColumnIndex(MEDICATION_IMAGE));
        if (image != null) {
            medication.setMedicationImage(BitmapFactory.decodeByteArray(image, 0, image.length));
        }

        image = c.getBlob(c.getColumnIndex(PRESCR_LABEL_IMAGE));
        if (image != null) {
            medication.setPrescrLabelImage(BitmapFactory.decodeByteArray(image, 0, image.length));
        }

        return medication;

    }

    /**
     * updateMedicationForiegnKey
     * This method updates the foreign key fields in the medication table
     * based on the type of foreign key that is passed in and value of key
     * @param foreignKeyDelete the key to delete
     * @param type family member or physician
     */
    public void updateMedicationForeignKey(int foreignKeyDelete, int type) {
        ContentValues cv = new ContentValues();
        String whereClause;
        if (foreignKeyDelete > 0) {

            switch (type) {
                case Constants.NAME_FAMILY_MEMBER: {
                    cv.put(FAMILY_MEMBER_PID, 0);
                    cv.put(FAMILY_MEMBER_NAME, "");
                    whereClause = FAMILY_MEMBER_PID + " = " + foreignKeyDelete;
                    break;
                }
                case Constants.NAME_PHYSICIAN: {
                    cv.put(PHYSICIAN_PID, 0);
                    cv.put(PHYSICIAN_NAME, "");
                    whereClause = PHYSICIAN_PID + " = " + foreignKeyDelete;
                    break;
                }
                default: {
                    return;
                }
            }

            update(cv, whereClause);
        }
    }

}
