package data;

/**
 * Created by Ironman on 4/13/2017.
 * @author Otto L. Lecuona
 */

public class Constants {
    public static final int         REQUEST_DOSAGE = 1000;
    public static final int         NAME_REQUEST = 2000;
    public static final int         REQUEST_PRIMARY_KEY = 3000;
    public static final int         REQUEST_DELETE = 4000;
    public static final int         REQUEST_CAMERA = 5000;

    public static final String      FREQ = "freq";
    public static final String      INTERVAL = "interval";
    public static final String      EVENT = "event";

    public static final String      MODE = "mode";
    public static final int         INSERT = 0;
    public static final int         UPDATE = 1;
    public static final int         ERROR = 1;
    public static final int         SUCCESS = 0;
    public static final String      PRIMARY_KEY = "pkey";
    public static final int         NO_PRIMARY_KEY = -1;

    public static final String      DOSAGE = "dosage";
    public static final String      DOSAGE_STRENGTH_VALUE = "dosage_strength";
    public static final String      DOSAGE_UNIT_VALUE = "dosage_unit";
    public static final String      DEFAULT_DOSAGE_UNIT = "mg";

    public static final String      NAME_TYPE = "name_type";
    public static final int         NAME_PHYSICIAN = 0;
    public static final int         NAME_FAMILY_MEMBER = 1;
    public static final int         NO_NAME_TYPE = 2;
    public static final String      RETURNED_NAME = "name";
    public static final String      TITLE_FOR_FAMILY_MEMBER_NAME = "Name of family member";
    public static final String      TITLE_FOR_PHYSICIAN_NAME = "Name of physcian";
    public static final int         STRENGTH = 0;
    public static final int         UNIT = 1;

    public static final int         PRIMARY_KEY_REQUESTED = 1;
    public static final int         PRIMARY_KEY_NOT_REQUESTED = 0;
    public static final int         SORT_TYPE_ALL = 2;
    public static final int         SORT_TYPE_FAMILY_MEMBER = 1;
    public static final int         SORT_TYPE_PHYSICIAN = 0;


    public static final String      DELETE = "delete";
    public static final int         DELETE_REQUESTED = 2;
    public static final int         NAME_REQUESTED = 3;
    public static final int         UPDATE_REQUESTED = 4;
    public static final int         DELETE_NOT_REQUESTED = 0;
    public static final int         NO_REQUEST = 5;

    public static final String      DATE_FORMAT = "MM-dd-yyyy";
    public static final String      TIME_FORMAT = "hh:mm aa";
    private static final String     DATE_TIME_FORMAT = DATE_FORMAT + " " + TIME_FORMAT;

    public static final String      EMPTY_STRING = "";

}
