package utility;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Ironman on 4/14/2017.
 * This class contains utilites associated with the keyboard
 */

public class KeyBoardUtil {

    /**
     * hideKeyboard
     * This method is used to hide the keyboard. One use is to hide the keyboard after an input
     * session is completed on the activity screen. The user can then navigate a clean screen.
     * Find the currently focused view, so we can grab the correct window token from it.
     * If no view currently has focus, create a new one, just so we can grab a window token from it
     * @param activity Activity
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
