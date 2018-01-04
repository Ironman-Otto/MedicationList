package data;

import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by Ironman on 4/13/2017.
 * This class groups together all database modules into one collection
 */

public class Database {

    public static MedsDb            medTable;
    public static FamilyMemberDb    familyTable;
    public static PhysicianDb       physicianTable;

    public Database (SQLiteDatabase db){
        medTable        = new MedsDb(db);
        familyTable     = new FamilyMemberDb(db);
        physicianTable  = new PhysicianDb(db);
    }

    /**
     * displayTransactionResults
     * Displays a Snack with message of fail or success based on value of rowId
     * none zero rowId indicates the operation was successful
     * @param rowId int
     * @param view  View
     * @param mode int
     */
    public static void displayTransactionResults(long rowId, View view, int mode ) {

        String message;
        message = (mode == Constants.UPDATE) ? "Update " : "Insert ";
        message += (rowId > 0) ? "successful" : "failed";

        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
}
