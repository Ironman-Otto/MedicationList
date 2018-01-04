package utility;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * Created by Ironman on 5/2/2017.
 * @author Otto L. Lecuona
 * This class contains application utilities
 */

public class AppUtil {
    /**
     * Api version of build
     * @return sdk version as an int
     */
    public static int getApiVersion() {

        return Build.VERSION.SDK_INT;
    }

    /**
     * Exit the application
     * @param context of application
     */
    public static void exitThisApp(Context context) {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    /**
     * Check if specified permission has been granted
     * @param permission to be verified
     * @param context of application
     * @return true if there is premission, false if not
     */
    private static boolean checkPermission(String permission, Context context) {

        int res = context.checkCallingOrSelfPermission(permission);

        return (res == PackageManager.PERMISSION_GRANTED);
    }

}
