package utility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;

import com.lsdinfotech.medicationlist.R;

import data.Constants;

/**
 * Created by Ironman on 5/2/2017.
 * @author Otto L. Lecuona
 * This class contains phone related utility functions
 */

public class PhoneUtil {

    /**
     * formatPhoneNumber
     * This method formats a string containing a phone number into
     * a standard format to use in making a call.
     * @param context of application
     * @param phone string containing unformated phone number
     * @return string with formated phone number
     */
    public static String formatPhoneNumber(Context context, String phone) {
        String formattedNumber = Constants.EMPTY_STRING;

        if (!StringUtil.isNullOrEmpty(phone)) {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                formattedNumber = PhoneNumberUtils.formatNumber(phone);
            }else{
                TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                String countryIsoCode = tm.getNetworkCountryIso().toUpperCase();
                formattedNumber = PhoneNumberUtils.formatNumber(phone, countryIsoCode);
            }

        }

        return formattedNumber;
    }

    /**
     * unFormatPhoneNumber
     * This method removes the formating character "-" in a  phone number
     * @param phoneNumber telephone number of the format xxx-xxx-xxxx
     * @return String of unformated phone number
     */
    public static String unFormatPhoneNumber(String phoneNumber) {

        String unFormattedNumber = Constants.EMPTY_STRING;

        if (!StringUtil.isNullOrEmpty(phoneNumber)) {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                unFormattedNumber = phoneNumber.replace("-","");
            }else{
                unFormattedNumber = PhoneNumberUtils.normalizeNumber(phoneNumber);
            }

        }

        return unFormattedNumber;
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

    /**
     * makeCall
     * This method makes a call to the specified phone number
     * it invokes a chooser and prepopulates the phone number
     * @param phoneNumber to call
     * @param context of application
     */
    public static void makeCall(String phoneNumber, Context context) {
        if(StringUtil.isNullEmptyBlank(phoneNumber)){
            ErrorUtil.alert(context,"Error", "There is no phone.");
        }
        else{
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse(phoneNumber));
            if(checkPermission("android.permission.CALL_PHONE",context))
                context.startActivity(callIntent);
        }
    }

    /**
     * dialCall
     * This method automatically dials the number passed in
     * @param phoneNumber to dial
     * @param context application context
     */
    public static void dialCall(String phoneNumber, Context context) {
        if(StringUtil.isNullEmptyBlank(phoneNumber)){
            ErrorUtil.alert(context,"Error", "There is no phone.");
        }
        else{
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse(phoneNumber));
            if(checkPermission("android.permission.CALL_PHONE",context))
                context.startActivity(callIntent);
        }
    }

    /**
     * confirmCallRequest
     * This method asks for a confirmation prior to dialing a call
     * @param phoneNumber number to be called
     * @param context application context
     */
    public static void confirmCallRequest(final String phoneNumber, final Context context) {
        if(StringUtil.isNotNullEmptyBlank(phoneNumber)) {
            okToDial(phoneNumber, context);
        } else {
            doNotdial(context);
        }
    }

    /**
     * okToDial
     * This method allow user to decide before placing call
     * @param phoneNumber to call
     * @param context application context
     */
    private static void okToDial(final String phoneNumber, final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.dialog_confirm))
                .setMessage(context.getResources().getString(R.string.dialog_ok_to_dial))
                .setPositiveButton(context.getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialCall(phoneNumber, context);
                    }
                })
                .setNegativeButton(context.getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
    }

    /**
     * doNotDial
     * This method displays message phone not on file
     * @param context application context
     */
    private static void doNotdial(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Phone number")
                .setMessage("Not on file")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();;
    }
}
