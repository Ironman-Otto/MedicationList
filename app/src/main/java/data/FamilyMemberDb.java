package data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import model.FamilyMember;

/**
 * Created by Ironman on 4/17/2017.
 * This class handles the family member table
 */

public class FamilyMemberDb {

    private SQLiteDatabase mDb;

    private static final String DATABASE_TABLE_FAMILY_MEMBER = "tbl_family_member";
    private static final String PRI_KEY_FAMiLY_MEMBER = "pk_id";
    private static final String NAME_PREFIX = "name_prefix";
    private static final String FIRST_NAME = "first_name";
    private static final String MIDDLE_NAME = "middle_name";
    private static final String LAST_NAME = "last_name";
    private static final String NAME_SUFFIX = "name_suffix";
    private static final String BIRTHDAY = "birthdate";
    private static final String FAMILY_MEMBER_IMAGE = "family_member_image";
    private static final String SEX = "sex";
    private static final String HEIGHT = "height";
    private static final String WEIGHT = "weight";
    private static final String BLOOD_TYPE = "blood_type";

    static final String CREATE_TABLE_FAMILY_MEMBER =
            "create table " + DATABASE_TABLE_FAMILY_MEMBER + " ("
                    + PRI_KEY_FAMiLY_MEMBER + " integer primary key autoincrement, "
                    + NAME_PREFIX + " text, "
                    + FIRST_NAME + " text, "
                    + MIDDLE_NAME + " text, "
                    + LAST_NAME + " text not null, "
                    + NAME_SUFFIX + " text, "
                    + BIRTHDAY + " text, "
                    + FAMILY_MEMBER_IMAGE + " blob, "
                    + SEX + " text, "
                    + HEIGHT + " text, "
                    + WEIGHT + " text, "
                    + BLOOD_TYPE + " text"
                    + ");";

    /**
     * This is the constructor method and sets
     * the database to be used.
     * @param mDb SQLiteDatabase
     */
    FamilyMemberDb(SQLiteDatabase mDb) {
        this.mDb = mDb;
    }

    /**
     * loadCv
     * This method loads the content values for the family member
     * @param fm FamilyMember
     * @return cv ContentValues
     */
    private ContentValues loadCv(FamilyMember fm) {
        ContentValues cv = new ContentValues();

        cv.put(NAME_PREFIX, fm.getPrefix());
        cv.put(FIRST_NAME, fm.getFirstName());
        cv.put(MIDDLE_NAME, fm.getMiddleName());
        cv.put(LAST_NAME, fm.getLastName());
        cv.put(NAME_SUFFIX, fm.getSuffix());
        cv.put(BIRTHDAY, fm.getBirthdate());
        cv.put(SEX, fm.getGender());
        cv.put(HEIGHT, fm.getHeight());
        cv.put(WEIGHT, fm.getWeight());
        cv.put(BLOOD_TYPE, fm.getBloodType());

        if ( fm.getFamilyMemberImage() != null ) {
            cv.put(FAMILY_MEMBER_IMAGE,convertBitmapByteArray(fm.getFamilyMemberImage()));
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
     * This method inserts a family member into the database
     * @param familyMember FamilyMember
     * @return int row_id
     */
    public int insert(FamilyMember familyMember) {

        ContentValues cv = loadCv(familyMember);
        return (int)mDb.insert(DATABASE_TABLE_FAMILY_MEMBER, null, cv);
    }

    /**
     * update
     * Update family member in database
     * @param familyMember Medication object
     * @return rowId int
     */
    public int update( FamilyMember familyMember ) {

        ContentValues cv = loadCv( familyMember );
        String whereClause = PRI_KEY_FAMiLY_MEMBER + " = " + familyMember.getPrimaryKey();
        return mDb.update(DATABASE_TABLE_FAMILY_MEMBER, cv, whereClause, null );
    }

    /**
     * query
     * Query the family member table for a family member
     * @param rowId int
     * @return Cursor
     */
    private Cursor query(int rowId) {

        String whereClause = PRI_KEY_FAMiLY_MEMBER + " = " + rowId;
        return mDb.query(DATABASE_TABLE_FAMILY_MEMBER, null, whereClause, null, null, null, null);

    }

    /**
     * queryAll
     * Query the family member table for all family members
     * @return Cursor
     */
    private Cursor queryAll() {
        return  mDb.query(DATABASE_TABLE_FAMILY_MEMBER, null, null, null, null, null, null);

    }

    /**
     * delete
     * This method deletes a specific row in database family member table
     * @param rowid tp be deleted
     * @return int
     */
    public int delete(int rowid) {
        String whereClause = PRI_KEY_FAMiLY_MEMBER + " = " + rowid;
        return mDb.delete(DATABASE_TABLE_FAMILY_MEMBER, whereClause, null);
    }

    /**
     * queryWhere
     * Query medication table using where clause
     * @param whereClause specify family members
     * @return cursor with family members
     */
    private Cursor queryWhere(String whereClause) {
        return mDb.query(DATABASE_TABLE_FAMILY_MEMBER, null, whereClause, null, null, null, null);
    }

    /**
     * queryFamilyMembers
     * Query the  FamilyMember table. Read all records or only one.
     * @param allRecords boolean
     * @param rowId int
     * @return familyMembers ArrayList<FamilyMember>
     */
    public ArrayList<FamilyMember> queryFamilyMembers(boolean allRecords, int rowId, String whereClause ) {

        ArrayList<FamilyMember> familyMembers = new ArrayList<>();
        FamilyMember familyMember;
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
                familyMember = readFamilyMemberCursor(c);
                familyMembers.add(familyMember);
            }
            while ( c.moveToNext() );
        }

        return familyMembers;
    }

    /**
     * readFamilyMemberCursor
     * This method reads one item from the curser
     * @param c Cursor with family member information
     * @return familyMember
     */
    private FamilyMember readFamilyMemberCursor(Cursor c) {
        FamilyMember familyMember = new FamilyMember();

        familyMember.setPrimaryKey(c.getInt(c.getColumnIndex(PRI_KEY_FAMiLY_MEMBER)));
        familyMember.setPrefix(c.getString(c.getColumnIndex(NAME_PREFIX)));
        familyMember.setFirstName(c.getString(c.getColumnIndex(FIRST_NAME)));
        familyMember.setMiddleName(c.getString(c.getColumnIndex(MIDDLE_NAME)));
        familyMember.setLastName(c.getString(c.getColumnIndex(LAST_NAME)));
        familyMember.setSuffix(c.getString(c.getColumnIndex(NAME_SUFFIX)));
        familyMember.setBirthdate(c.getString(c.getColumnIndex(BIRTHDAY)));
        familyMember.setBloodType(c.getString(c.getColumnIndex(BLOOD_TYPE)));
        familyMember.setHeight(c.getString(c.getColumnIndex(HEIGHT)));
        familyMember.setWeight(c.getString(c.getColumnIndex(WEIGHT)));
        familyMember.setGender(c.getString(c.getColumnIndex(SEX)));

        byte[] image = c.getBlob(c.getColumnIndex(FAMILY_MEMBER_IMAGE));
        if (image != null) {
            familyMember.setFamilyMemberImage(BitmapFactory.decodeByteArray(image, 0, image.length));
        }

        return familyMember;
    }
}
