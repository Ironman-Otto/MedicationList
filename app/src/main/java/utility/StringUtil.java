package utility;

/**
 * Created by Ironman on 4/14/2017.
 * This class is used to implement a collection of String utilities
 */

public class StringUtil {
    /**
     * capFirstCharString()
     * Capitalizes the first character in a String
     * @param s String
     * @return str String modifid
     */
    public static String capFirstCharString(String s){

        String str = s;
        if(!isNullOrEmpty(str)) {
            Character firstChar = str.charAt(0);
            String c1 = firstChar.toString();
            String capC1 = c1.toUpperCase();
            String sub = str.substring(1);
            str = capC1 + sub;
        }

        return str;
    }

    /**
     * isNull
     * Chech a String for a null value. It is recommended
     * to check for nulls before performing operations that
     * may result in a NullPointerException
     * @param s String
     * @return boolean
     */
    public static boolean isNull(String s) {
        return s == null;
    }

    /**
     * isNullOrEmpty()
     * This is the safe way to check a String and avoid a NullPointerException
     * This method returns true if the string is null or empty. Must always check
     * for null condition before using .isEmpty().
     * @param s String
     * @return boolean
     */
    public static boolean isNullOrEmpty (String s){

        String str = s;
        if(str == null){
            return true;
        }
        else if (str.isEmpty()){
            return true;
        }
        else {
            return false;
        }

    }

    /**
     * isBlank
     * This method checks for a blank string. The String is trimmed of leading/trailing
     * blanks.
     * @param s String
     * @return true if string is blank false if not
     */
    public static boolean isBlank(String s) {

        return s.trim().isEmpty();
    }

    /**
     * isNullEmptyBlank
     * This method checks for all three conditions on a string
     * Returns true for null, empty, or blank string
     * @param s String
     * @return boolean result
     */
    public static boolean isNullEmptyBlank(String s) {

        return s == null || s.isEmpty() || s.trim().isEmpty();
    }

    /**
     * isNotNullEmptyBlank
     * This method checks a string for a value;
     * Returns true if the  string has a value, false if string
     * is null, empty, or blank
     * @param s
     * @return
     */
    public static boolean isNotNullEmptyBlank(String s) {

        return s != null && !s.isEmpty() && !s.trim().isEmpty();
    }

    /**
     * parseString extracts the tokens from a string according to regular expression
     * example to use a space as a delimiter we would set delim to "[ ]"
     * @param sin String
     * @param delim String
     * @return String[]
     */
    public static String[] parseString(String sin, String delim) {

        return sin.split(delim);
    }
}
