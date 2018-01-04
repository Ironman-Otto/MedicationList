package model;

/*
 * Created by Ironman on 6/8/2017.
 * @author Otto L. Lecuona
 */

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import utility.StringUtil;

public class MyEvent {
    private long                    eventId;
    private String                  eventTitle;
    private long                    dtstart;
    private String                  time;
    private String                  date;
    private String                  rrule;
    private String                  freq;
    private String                  byDay;
    private String                  interval;
    private String                  count;
    private String                  byDayExpanded;
    private String                  until;
    private static final String     DATE_FORMAT = "MM-dd-yyyy";
    private static final String     TIME_FORMAT = "hh:mm aa";
    private static final String     DATE_TIME_FORMAT = DATE_FORMAT + " " + TIME_FORMAT;
    private static final String     DEFAULTCOUNT = "Indefinitely";
    private boolean                 isRecurring;

    public MyEvent(long eventId, String eventTitle, long dtstart, String rrule) {
        this.eventId    = eventId;
        this.eventTitle = eventTitle;
        this.dtstart    = dtstart;
        this.rrule      = rrule;

        if (rrule != null) {
            isRecurring = true;
            parseRrule();
            expandByDay();
        } else {
            isRecurring = false;
        }
    }

    public MyEvent() {

    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public long getDtstart() {
        return dtstart;
    }

    public void setDtstart(long dtstart) {
        this.dtstart = dtstart;
    }

    public String getRrule() {
        return rrule;
    }

    public void setRrule(String rrule) {
        this.rrule = rrule;
    }

    public String getFreq() {
        return freq;
    }

    public void setFreq(String freq) {
        this.freq = freq;
    }

    public void setByDay(String byDay) {
        this.byDay = byDay;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public void setByDayExpanded(String byDayExpanded) {
        this.byDayExpanded = byDayExpanded;
    }

    public String getByDay() {
        return byDay;
    }

    public String getInterval() {
        return interval;
    }

    public String getCount() {
        return count;
    }

    public String getByDayExpanded() {
        return byDayExpanded;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    public String getUntil() {
        return until;
    }

    public void setUntil(String until) {
        this.until = until;
    }

    /**
     * parseRrule
     * This method analyzes the RRULE in use and separates it into
     * the variouw individual components. These components are then
     * available through getter methods.
     */
    public void parseRrule() {
        String rule = rrule.replace(";", " ");
        String token[] = StringUtil.parseString(rule, "[ ]");
        String[] ruleArray = new String[token.length];

        for(int i = 0; i < token.length; i++){
            String[] tokenParsed = StringUtil.parseString(token[i], "[=]");
            ruleArray[i] = tokenParsed[1];
            splitRRule(i, tokenParsed[0], ruleArray);
        }

    }

    /**
     * splitRRule
     * This method looks at the array of rule bodies;
     * the part after the = sign, i.e. FREQ=DAILY would deliver the
     * word DAILY. The case statemtents cover
     * Freq, byday, interval, count
     * @param i contains index into array containing values for tag
     * @param token contains TAG
     * @param ruleArray contains value for tag
     */
    private void splitRRule(int i, String token, String[] ruleArray) {
        switch (token.toUpperCase()){
            case "FREQ":{
                freq = ruleArray[i];
                break;
            }
            case "BYDAY":{
                byDay = ruleArray[i];
                break;
            }
            case "INTERVAL":{
                interval = ruleArray[i];
                break;
            }
            case "COUNT":{
                count = ruleArray[i];
                break;
            }
            case "UNTIL":{
                until = ruleArray[i];
                break;
            }
        }
    }

    /**
     * expandByDay
     * This method converts the BYDAY component of the RRULE to full day names
     * for the UI. This is a user friendly component
     */
    public void expandByDay() {

        byDayExpanded = "";
        String[] token = null;
        if (StringUtil.isNotNullEmptyBlank(byDay)) {
            token = StringUtil.parseString(byDay, "[,]");

            for (int i = 0; i < token.length; i++) {
                if (token[i].toUpperCase().equals("SU")) {
                    byDayExpanded = "Sunday ";
                } else if (token[i].toUpperCase().equals("MO")) {
                    byDayExpanded = byDayExpanded + "Monday ";
                } else if (token[i].toUpperCase().equals("TU")) {
                    byDayExpanded = byDayExpanded + "Tuesday ";
                } else if (token[i].toUpperCase().equals("WE")) {
                    byDayExpanded = byDayExpanded + "Wednesday ";
                } else if (token[i].toUpperCase().equals("TH")) {
                    byDayExpanded = byDayExpanded + "Thursday ";
                } else if (token[i].toUpperCase().equals("FR")) {
                    byDayExpanded = byDayExpanded + "Friday ";
                } else if (token[i].toUpperCase().equals("SA")) {
                    byDayExpanded = byDayExpanded + "Saturday ";
                }
            }
        }

        byDayExpanded = byDayExpanded.trim();
        byDayExpanded = byDayExpanded.replace(" ", ",");
    }

    /**
     * getMillis
     * This method converts the time and date string values of the event
     * into milliseconds
     * @return long milliseconds
     */
    public long getMillis() {
        DateFormat dateFormat1 = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault());
        String dateString = date + " " + time;
        try {
            Date startDate = dateFormat1.parse(dateString);
            return startDate.getTime();
        } catch (ParseException pe){
            return 0;
        }
    }

    /**
     * generateRRule
     * This method generates the RRULE for the event based on inputs from
     * the user.
     * @return String with RRULE
     */
    public String generateRRule() {
        return generateFreqTag()
                + generateIntervalTag()
                + generateByDayTag()
                + generateCountTag()
                + generateUntilTag();
    }
    /**
     * generateFreqTag
     * This method generates the FREQ tag based on the occurrence input.
     * It is the first component of the RRULE.
     * @return String FREQ tag
     */
    private String generateFreqTag() {
        return "FREQ=" + freq;
    }
    /**
     * generateByDayTag
     * This method takes the input occurance days and
     * generates the BYDAY tag value
     * The byDay string contains the sequence of full names of the
     * days of the week. The first two letters of each day is used
     * to generate the elements of the tag
     * @return String with completed BYDAY tag with values
     */
    private String generateByDayTag() {
        if(StringUtil.isNotNullEmptyBlank(byDay)) {

            String[] tokens = StringUtil.parseString(byDay, "[,]");
            String byDays = "";
            for (int i = 0; i < tokens.length; i++) {
                tokens[i] = tokens[i].trim();
                byDays = byDays + tokens[i].substring(0, 2).toUpperCase() + " ";
            }
            byDays = byDays.trim();
            byDays = byDays.replace(" ", ",");

            return ";BYDAY=" + byDays;
        } else {
            return "";
        }
    }

    /**
     * generateIntervalTag
     * This method generates the INTERVAL tag based on the interval entered
     * in the Repeat every input. The format of the interval is
     * for example "2 Days". The input is split and the first element
     * is used to generate the INTERVAL tag element
     * @return INTERVAL tag
     */
    private String generateIntervalTag() {
        String intervalElement = StringUtil.parseString(interval, "[ ]")[0];

        if (StringUtil.isNotNullEmptyBlank(intervalElement)) {
            if (intervalElement.equals("1")) {
                return "";
            } else {
                return ";INTERVAL=" + intervalElement;
            }
        } else {
            return "";
        }

    }

    /**
     * generateCountTag
     * This method generates the COUNT tag based on the input
     * Number of occurrences. The format is "2 Times" except for the
     * first element of the choices which is "Indefinitely" that causes
     * the tag to be blank.
     * @return String COUNT tag with element or empty
     */
    private String generateCountTag() {

        if((StringUtil.isNotNullEmptyBlank(count) && count.equals(DEFAULTCOUNT)) ||
                StringUtil.isNullEmptyBlank(count)) {
                return "";
        } else {
            String countElement = StringUtil.parseString(count, "[ ]")[0];
            return ";COUNT=" + countElement;
        }
    }

    private String generateUntilTag() {
        if(StringUtil.isNullEmptyBlank(until)){
            return "";
        } else {
            String[] untilElements = StringUtil.parseString(until, "[-]");
            return ";UNTIL=" + untilElements[2] + untilElements[0] + untilElements[1] + "T235900Z";
        }
    }

    /**
     * calculateEventTime
     * This method formats the incoming milliseconds into a formatted string
     * @param format format to use in creating the date string
     * @return String formated date
     */
    public String calculateEventTime(String format) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dtstart);
        Date dt = cal.getTime();
        DateFormat df = new SimpleDateFormat(format, Locale.getDefault());
        return df.format(dt);
    }

    /**
     * generateOccursOn
     * This method generates the message for the occurs on days
     * @return list of weekdays for event
     */
    public String generateOccursOn() {
        switch (freq){
            case "DAILY":{
                return generateMessage("day on", "day");
            }
            case "WEEKLY":{
                return generateMessage("week on ", "week");
            }
            case "MONTHLY":{
                return generateMessage("month on every", "month on day");
            }
            case "YEARLY":{
                return generateMessage("year on every", "year on");
            }

            default:{
                return byDayExpanded;
            }
        }
    }

    /**
     * generateMessage
     * This method generates the full message associated with the
     * occurs on field
     * @param message for rrules with byday values
     * @param message2 for rrules with no byday
     * @return full message to display
     */
    private String generateMessage(String message, String message2) {
        if (StringUtil.isNotNullEmptyBlank(byDayExpanded)) {
            return generateIntervalMsg()
                    + message + " "
                    + byDayExpanded + " "
                    + generateCountMsg()
                    + generateUntilMsg();
        }
        else {
            return generateIntervalMsg()
                    + message2 + " "
                    + generateFreqMsgDate()
                    + generateCountMsg()
                    + generateUntilMsg();
        }
    }

    /**
     * generateFreqMsgDate
     * This method calculates the date format for rrule
     * for monthly or yearly events
     * @return formated day of month or formated month day of year
     */
    private String generateFreqMsgDate() {
        String msgDate = "";
        switch (freq) {
            case "MONTHLY":{
                msgDate = calculateEventTime("dd") + " ";
                break;
            }
            case "YEARLY":{
                msgDate = calculateEventTime("MM-dd") + " ";
                break;
            }
        }
        return msgDate;
    }

    /**
     * generateIntervalMsg
     * This method generates the message associated with the rrule interval
     * @return string with interval message
     */
    private String generateIntervalMsg() {
        StringBuilder intervalMsg = new StringBuilder();
        intervalMsg.append("every ");

        if (StringUtil.isNullEmptyBlank(interval)) {
            return intervalMsg.toString();
        }

        switch (interval) {
            case "1":{
                break;
            }
            case "2":{
                intervalMsg.append("other ");
                break;
            }
            case "3":{
                intervalMsg.append("3rd ");
                break;
            }
            case "23":{
                intervalMsg.append(interval);
                intervalMsg.append("rd ");
                break;
            }
            case "21":
            case "31":{
                intervalMsg.append(interval);
                intervalMsg.append("st ");
                break;
            }
            default:{
                intervalMsg.append(interval);
                intervalMsg.append("th ");
            }
        }

        return intervalMsg.toString();
    }

    /**
     * generateCountMsg
     * This method generates a message for the current event count
     * @return string with number of times event repeats
     */
    private String generateCountMsg() {
        if (StringUtil.isNullEmptyBlank(count)){
            return "";
        } else {
            return "for " + count + " times";
        }
    }

    /**
     * generateUntilMsg
     * This method returns the until date if exits
     * @return string until date
     */
    private String generateUntilMsg() {
        if (StringUtil.isNullEmptyBlank(until)){
            return "";
        } else {
            return " until " + formatUntilDate();
        }
    }

    /**
     * formatUntilDate
     * This method converts the iCS Until date string to
     * a friendly format
     * yyyymmddT000000Z -> mm-dd-yyyy
     * @return formated date string
     */
    public String formatUntilDate() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(until.substring(4, 6)); //month
        stringBuilder.append("-");
        stringBuilder.append(until.substring(6, 8)); //day
        stringBuilder.append("-");
        stringBuilder.append(until.substring(0, 4)); //year

        return stringBuilder.toString();
    }

}
