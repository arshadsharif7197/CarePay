package com.carecloud.carepaylibray.utils;

import android.util.Log;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kkannan on 9/13/16.
 * Helper class to format the date
 *
 * @link (to be assigned)
 */
public class DateUtil {

    public static final String TAG = "DateUtil";
    private static final String FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String FORMAT_YYYY_DASH_MM_DASH_DD = "yyyy-MM-dd";
    private static final String FORMAT_MM_DASH_DD_DASH_YYYY = "MM-dd-yyyy";
    private static final String FORMAT_MM_SLASH_DD_SLASH_YYYY = "MM/dd/yyyy";

    private static DateUtil instance;
    private String[] formats;
    private Date   date;
    private int    day;
    private int    month; // Attention! 0-indexed month
    private int    year;
    private int    hour12;
    private int    minute;
    private String dayLiteral;
    private String monthLiteral;
    private String dayLiteralAbbr;
    private String monthLiteralAbbr;
    private String amPm;

    /**
     * Returns the instance of this singleton
     *
     * @return The instance
     */
    public static DateUtil getInstance() {
        if (instance == null) {
            instance = new DateUtil();
            // by default set the date to current date
            instance.setDate(Calendar.getInstance(Locale.getDefault()).getTime());

            instance.formats = new String[] {
                    FORMAT_ISO_8601,
                    FORMAT_YYYY_DASH_MM_DASH_DD,
                    FORMAT_MM_DASH_DD_DASH_YYYY,
                    FORMAT_MM_SLASH_DD_SLASH_YYYY
            };
        }
        return instance;
    }

    /**
     * Set the a date to be formatted;
     * The expected format any of the following formats
     * yyyy-MM-dd'T'HH:mm:ssZ
     * yyyy-MM-dd
     * MM-dd-yyyy
     * MM/dd/yyyy
     *
     * @param dateString The date as string
     * @return The current DateUtil object
     */
    public DateUtil setDateRaw(String dateString) {
        for (String format: formats) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
                formatter.setLenient(false);
                Date newDate = formatter.parse(dateString);
                setDate(newDate);

                return this;
            } catch (ParseException ignored) {
                Log.i(TAG, "Date string '" + dateString + "' is not in format " + format);
            }
        }

        Log.e(TAG, "Date string was not formatted in known formats");

        return this;
    }

    /**
     * Reset the current reference to the current date
     *
     * @return The instance
     */
    public DateUtil setToCurrent() {
        date = Calendar.getInstance().getTime();
        updateFields();
        return this;
    }

    /**
     * Reset the current reference to the tomorrow date
     *
     * @return The instance
     */
    public DateUtil setToDayAfterTomorrow() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 2);
        date = calendar.getTime();
        updateFields();
        return this;
    }

    /**
     * Format the date as "EEEE, MMMM d" (eg Monday, October 10th)
     *
     * @return A string containing the formatted date
     */
    public String getDateAsDayMonthDayOrdinal() {
        return String.format(Locale.getDefault(), "%s, %s %d%s",
                dayLiteral, monthLiteral, day, getOrdinalSuffix(day));
    }

    /**
     * Format the date as "EEEE, MMM d" (eg Monday, Oct 10th)
     *
     * @return A string containing the formatted date
     */
    public String getDateAsDayMonthDayOrdinalYear() {
        return String.format(Locale.getDefault(), "%s, %s %d%s",
                dayLiteral, monthLiteralAbbr, day, getOrdinalSuffix(day));
    }

    /**
     * Return the time as 12-hour (format "h:mm a")
     *
     * @return A string contains the formatted time
     */
    public String getTime12Hour() {
        return String.format(Locale.getDefault(), "%d:%02d %s", (hour12==0) ? 12 : hour12, minute, amPm);
    }

    /**
     * Format current date as Month, Day(ordinal) YYYY (eg October 3rd, 2016)
     *
     * @return The formatted date as string
     */
    public String getDateAsMonthLiteralDayOrdinalYear() {
        String ordinal = instance.getOrdinalSuffix(day); // fetch the ordinal
        return String.format(Locale.getDefault(), "%s %s%s, %d",
                monthLiteral, day, ordinal, year);
    }

    /**
     * Format current date as Month, Day(ordinal) (eg October 3rd)
     *
     * @return The formatted date as string
     */
    public String getDateAsMonthLiteralDayOrdinal() {
        String ordinal = instance.getOrdinalSuffix(day); // fetch the ordinal
        return String.format(Locale.getDefault(), "%s %s%s",
                monthLiteralAbbr, day, ordinal);
    }

    /**
     * Format date as MM-dd-yyyy
     *
     * @return The formatted date as string
     */
    public String toStringWithFormatMmDashDdDashYyyy() {
        return toStringWithFormat(FORMAT_MM_DASH_DD_DASH_YYYY);
    }

    /**
     * Format date as yyyy-MM-dd
     *
     * @return The formatted date as string
     */
    public String toStringWithFormatYyyyDashMmDashDd() {
        return toStringWithFormat(FORMAT_YYYY_DASH_MM_DASH_DD);
    }

    /**
     * Format date as MM/dd/yyyy
     *
     * @return The formatted date as string
     */
    public String toStringWithFormatMmSlashDdSlashYyyy() {
        return toStringWithFormat(FORMAT_MM_SLASH_DD_SLASH_YYYY);
    }

    /**
     * Transform date to format "yyyy-MM-dd'T'HH:mm:ssZ"
     *
     * @return The string
     */
    public String toStringWithFormatIso8601() {
        return toStringWithFormat(FORMAT_ISO_8601);
    }

    /**
     * Transform a date formatted as a format
     *
     * @param format The date to be formatted
     * @return The string
     */
    private String toStringWithFormat(String format) {
        return new SimpleDateFormat(format, Locale.getDefault()).format(this.date);
    }

    /**
     * Compare the date with another date
     *
     * @param date The date to compare to
     * @return An int: 1 if date is before, -1 after or 0 if equal
     */
    public int compareTo(Date date) {
        if (this.date.before(date)) {
            return -1;
        }
        if (this.date.after(date)) {
            return 1;
        }
        return 0;
    }

    public int getDay() {
        return day;
    }

    /**
     * Gets the 0-index month (January is 0, February 1 etc)
     *
     * @return The index
     */
    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getHour12() {
        return hour12;
    }

    public int getMinute() {
        return minute;
    }

    public String getDayLiteral() {
        return dayLiteral;
    }

    public String getMonthLiteral() {
        return monthLiteral;
    }

    public String getDayLiteralAbbr() {
        return dayLiteralAbbr;
    }

    public String getMonthLiteralAbbr() {
        return monthLiteralAbbr;
    }

    public String getAmPm() {
        return amPm;
    }

    /**
     * Get the date the util works on
     *
     * @return The date
     */
    public Date getDate() {
        return date;
    }

    private DateUtil() {
        this.date = new Date();
    }

    public void setDate(Date date) {
        this.date = date;
        updateFields();
    }

    public void setDate(Calendar calendar){
        this.date = calendar.getTime();
        updateFields(calendar);
    }

    /**
     * Get the ordinal suffix
     *
     * @param number the last digit of the day (as char)
     * @return return a ordinal String with day
     */
    public String getOrdinalSuffix(int number) {
        int lastTwoDigits = number % 100;
        int dayLastDigit = number % 10;
        if (dayLastDigit == 1) { //
            if (lastTwoDigits == 11) { // if it ends in 11, use 'th'
                return "th";
            } else {
                return "st"; // if end in 1, use 'st'
            }
        } else if (dayLastDigit == 2) {
            if (lastTwoDigits == 12) { // if it ends in 12, use 'th'}
                return "th";
            } else { // if ends just in 2, use "nd"
                return "nd";
            }
        } else if (dayLastDigit == 3) {
            if (lastTwoDigits == 13) {
                return "th"; // if last two digits are 13, use 'th'
            } else {
                return "rd"; // else use 'rd'
            }
        } else {
            return "th";
        }
    }

    private void updateFields() {
        if (date != null) {
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.setTime(date);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            month = calendar.get(Calendar.MONTH);
            year = calendar.get(Calendar.YEAR);
            hour12 = calendar.get(Calendar.HOUR);
            minute = calendar.get(Calendar.MINUTE);
            amPm = DateFormatSymbols.getInstance(Locale.getDefault()).getAmPmStrings()[calendar.get(Calendar.AM_PM)];
            dayLiteral = DateFormatSymbols.getInstance(Locale.getDefault()).getWeekdays()[calendar.get(Calendar.DAY_OF_WEEK)];
            monthLiteral = DateFormatSymbols.getInstance(Locale.getDefault()).getMonths()[month];
            dayLiteralAbbr = DateFormatSymbols.getInstance(Locale.getDefault()).getShortWeekdays()[calendar.get(Calendar.DAY_OF_WEEK)];
            monthLiteralAbbr = DateFormatSymbols.getInstance(Locale.getDefault()).getShortMonths()[month];
        }
    }

    private void updateFields(Calendar calendar) {
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        hour12 = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);
        amPm = DateFormatSymbols.getInstance(Locale.getDefault()).getAmPmStrings()[calendar.get(Calendar.AM_PM)];
        dayLiteral = DateFormatSymbols.getInstance(Locale.getDefault()).getWeekdays()[calendar.get(Calendar.DAY_OF_WEEK)];
        monthLiteral = DateFormatSymbols.getInstance(Locale.getDefault()).getMonths()[month];
        dayLiteralAbbr = DateFormatSymbols.getInstance(Locale.getDefault()).getShortWeekdays()[calendar.get(Calendar.DAY_OF_WEEK)];
        monthLiteralAbbr = DateFormatSymbols.getInstance(Locale.getDefault()).getShortMonths()[month];

    }

    /**
     * Check for Today.
     *
     * @return true if today
     */
    public boolean isToday() {
        Calendar calendar = Calendar.getInstance();
        int crtDay = calendar.get(Calendar.DAY_OF_MONTH);
        int crtMonth = calendar.get(Calendar.MONTH);
        int crtYear = calendar.get(Calendar.YEAR);

        return crtDay == day && crtMonth == month && crtYear == year;
    }

    public static boolean isToday(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return isToday(calendar);
    }

    public static boolean isToday(Calendar calendar){
        Calendar checkCal = Calendar.getInstance();//init to today
        return isSameYear(calendar, checkCal) && isSameDay(calendar, checkCal);
    }

    /**
     * Check date is before or not.
     *
     * @return true if before
     */
    public boolean isYesterdayOrBefore() {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        int crtDay = calendar.get(Calendar.DAY_OF_MONTH);
        // check if crt date is after the date in the util and the days differ
        return compareTo(today) == -1;
    }

    /**
     * Check date is after or not.
     *
     * @return true if after
     */
    public boolean isTomorrowOrAfter() {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        int crtDay = calendar.get(Calendar.DAY_OF_MONTH);
        // check if crt date is before the date in the util and the days differ
        return compareTo(today) == 1 && crtDay != day;
    }

    /**
     * Check date is tomorrow
     *
     * @return true if tomorrow
     */
    public boolean isTomorrow() {
        Calendar calendar = Calendar.getInstance();
        int crtDay = calendar.get(Calendar.DAY_OF_MONTH);
        int crtMonth = calendar.get(Calendar.MONTH);
        int crtYear = calendar.get(Calendar.YEAR);

        return crtDay == day-1 && crtMonth == month && crtYear == year;
    }

    public static boolean isTomorrow(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return isTomorrow(calendar);
    }

    public static boolean isTomorrow(Calendar calendar){
        Calendar checkCal = Calendar.getInstance();
        checkCal.add(Calendar.DAY_OF_YEAR, 1);

        return isSameDay(calendar, checkCal);

    }


    /**
     * Check if given date corresponds to last day in month
     *
     * @return true if date is last day in month
     */
    public boolean endsThisMonth(){

        return endsThisMonth(date);
    }

    public static boolean endsThisMonth(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return endsThisMonth(calendar);
    }

    public static boolean endsThisMonth(Calendar calendar){
        Calendar checkCal = Calendar.getInstance();
        int calMonth = checkCal.get(Calendar.MONTH);
        int calMaxDayMonth = checkCal.getActualMaximum(Calendar.DAY_OF_MONTH);

        return isSameYear(checkCal, calendar) &&
                calMonth == calendar.get(Calendar.MONTH) &&
                calMaxDayMonth == calendar.get(Calendar.DAY_OF_MONTH);
    }


    public static int getDaysElapsed(Date start, Date end){
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();

        startCal.setTime(start);
        endCal.setTime(end);

        return getDaysElapsed(startCal, endCal);
    }

    public static int getDaysElapsed(Calendar start, Calendar end){
        if(end.compareTo(start)<0){//parameters in wrong order
            Log.w(TAG, "calendar parameters out of order" );
            Calendar temp = start;
            start = end;
            end = temp;
        }

        int years = end.get(Calendar.YEAR) - start.get(Calendar.YEAR);
        if(years == 0) {//is the time period within the current year range
            return end.get(Calendar.DAY_OF_YEAR) - start.get(Calendar.DAY_OF_YEAR);
        }else if(years == 1){//this is probably a care where the range is carrying over at the end of the year
            int daysLeftStartingYear =  start.getActualMaximum(Calendar.DAY_OF_YEAR) - start.get(Calendar.DAY_OF_YEAR);
            return daysLeftStartingYear + end.get(Calendar.DAY_OF_YEAR);
        }else{//need to figure out how many years we are looking at
            int fullYearCount = years-1;

            int leapYearOffset = 0;
            int yearCheck = start.get(Calendar.YEAR);
            for(int i=0; i<fullYearCount; i++){
                yearCheck++;
                if(yearCheck%4==0)
                    leapYearOffset++;
            }

            int daysLeftStartingYear = start.getActualMaximum(Calendar.DAY_OF_YEAR) - start.get(Calendar.DAY_OF_YEAR);
            return daysLeftStartingYear + fullYearCount * 365 + end.get(Calendar.DAY_OF_YEAR) + leapYearOffset;
        }
    }

    public static int getDaysElapsedInclusive(Date start, Date end){
        return getDaysElapsed(start, end)+1;
    }

    public static int getDaysElapsedInclusive(Calendar start, Calendar end){
        return getDaysElapsed(start, end)+1;
    }

    public static boolean isSameYear(Calendar start, Calendar end){
        return start.get(Calendar.YEAR) == end.get(Calendar.YEAR);
    }

    public static boolean isSameYear(Date start, Date end){
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();

        startCal.setTime(start);
        endCal.setTime(end);

        return isSameYear(startCal, endCal);
    }

    public static boolean isSameDay(Calendar start, Calendar end){
        return isSameYear(start, end) && start.get(Calendar.DAY_OF_YEAR) == end.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isSameDay(Date start, Date end){
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();

        startCal.setTime(start);
        endCal.setTime(end);

        return isSameDay(startCal, endCal);
    }

    /**
     * Verify if a date has mm/dd/yyyy format
     *
     * @param dateString The date whose format is to be changed
     * @return The dat in the new format as a string
     */
    public static boolean isValidateStringDateOfBirth(String dateString) {
        String formatString = "MM/dd/yyyy";
        try {
            SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.getDefault());
            format.setLenient(false);
            Date dob = format.parse(dateString);
            getInstance().setDate(dob);
            return getInstance().isYesterdayOrBefore() && getInstance().year > 1900;
        } catch (ParseException e) {
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Formats the month and year. If provided a separator then will be displayed as MM/yyyy or else
     * MMyyyy
     *
     * @param year        The year that was set.
     * @param monthOfYear The month that was set (0-11) for compatibility with {@link
     *                    Calendar}.
     * @return the formatted string
     */
    public String formatMonthYear(int year, int monthOfYear) {
        Locale locale = Locale.getDefault();
        Calendar calendar = Calendar.getInstance(locale);
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        SimpleDateFormat format = new SimpleDateFormat(
                "MM/yyyy", Locale.getDefault());
        return format.format(calendar.getTime());
    }

    /**
     * Format the date as "EEEE, MMMM d" (eg Monday, Oct 10th)
     *
     * @return A string containing the formatted date
     */
    public String getDateAsDayShortMonthDayOrdinal() {
        return String.format(Locale.getDefault(), "%s, %s %d%s",
                dayLiteral, monthLiteralAbbr, day, getOrdinalSuffix(day));
    }


}