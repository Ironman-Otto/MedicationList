package utility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

/**
 * Utility functions on error.
 *
 * @author Otto L. Lecuona
 * used some functions from ErrorUtil by umbalaconmeogia
 *
 */
public class ErrorUtil {

    /**
     * Wrap an exception by RuntimeException.
     * <p>
     * The exception is not wrapped if it has already been a RuntimeException.
     * @param e The exception to be wrapped.
     * @return A RuntimeException object.
     */
    public static RuntimeException runtimeException(Exception e) {
        return e instanceof RuntimeException ?
                (RuntimeException) e :
                new RuntimeException("Exception wrapped by RuntimeException", e);
    }

    /**
     * Display a message on a dialog.
     * @param context
     * @param title
     * @param message
     */
    public static void alert(Context context, String title, String message) {
        alert(context, title, message, "OK");
    }


    /**
     * Display a message on a dialog.
     * @param context
     * @param title
     * @param message
     * @param buttonTitle
     */
    public static void alert(Context context, String title, String message,
                             String buttonTitle) {
        alert(context, title, message, buttonTitle, null);
    }

    /**
     * Display a message with one action button
     * @param context
     * @param title
     * @param message
     * @param buttonTitle
     * @param listener
     */
    public static void alert(Context context, String title, String message,
                             String buttonTitle, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setTitle(title)
                .setPositiveButton(buttonTitle, listener)
                .show();
    }

    /**
     * This function will create a dialog box with two buttons, positive, negative
     * @param context
     * @param title
     * @param message
     * @param btnPosTitle
     * @param listenerPos
     * @param btnNegTitle
     * @param listenerNeg
     * @param cancelable
     */
    public static void alert(Context context, String title, String message,
                             String btnPosTitle, DialogInterface.OnClickListener listenerPos,
                             String btnNegTitle, DialogInterface.OnClickListener listenerNeg,
                             boolean cancelable) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setTitle(title)
                .setPositiveButton(btnPosTitle,listenerPos)
                .setNegativeButton(btnNegTitle,listenerNeg)
                .setCancelable(cancelable)
                .show();
    }

    /**
     * This function is used to exit the application using the intent mechanism
     * @param cancelable
     * @param context
     */
    public static void confirmExit(boolean cancelable, final Context context) {
        final Context ctx = context;

        ErrorUtil.alert(context, "Confirm", "Want to exit app?",
                "YES",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
                , "NO",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                },cancelable);
    }





}
