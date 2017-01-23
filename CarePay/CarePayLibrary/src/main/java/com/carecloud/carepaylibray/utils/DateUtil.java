package com.carecloud.carepaylibray.utils;

import com.carecloud.carepay.service.library.CarePayConstants;

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

    private static DateUtil instance;
    private String format = CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT;
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
        }
        return instance;
    }

    /**
     * Set the a date to be formatted;
     * The expected format is the format previously set for the class with setFormat()
     * or the default "yyyy-MM-dd'T'HH:mm:ssZ"
     *
     * @param dateString The date as string
     * @return The current DateUtil object
     */
    public DateUtil setDateRaw(String dateString) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
            Date newDate = formatter.parse(dateString);
            setDate(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
        //date = Calendar.getInstance().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 2);
        date = calendar.getTime();
        updateFields();
        return this;
    }

    /**
     * Set the format of the in-coming date
     * By default this is "yyyy-MM-dd'T'HH:mm:ssZ".
     *
     * @param format The new format
     */
    public void setFormat(String format) {
        if (format != null) {
            instance.format = format;
        }
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
     * Format the date as "EEE, MMM d YYYY" (eg Mon, Oct 10th 2016)
     *
     * @return A string containing the formatted date
     */
    public String getDateAsDayMonthDayOrdinalYear() {
        return String.format(Locale.getDefault(), "%s, %s %d%s %s",
                             dayLiteralAbbr, monthLiteralAbbr, day, getOrdinalSuffix(day), year);
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
                monthLiteral, day, ordinal);
    }

    /**
     * Format date as MM-dd-yyyy
     *
     * @return The formatted date as string
     */
    public String getDateAsMMddyyyy() {
        return String.format(Locale.getDefault(), "%02d-%02d-%4d", month + 1, day, year);
    }

    /**
     * Format date as yyyy-MM-dd
     *
     * @return The formatted date as string
     */
    public String getDateAsyyyyMMdd() {
        return String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
    }

    /**
     * Format date as MM/dd/yyyy
     *
     * @return The formatted date as string
     */
    public String getDateAsMMddyyyyWithSlash() {
        return String.format(Locale.getDefault(), "%02d/%02d/%4d", month + 1, day, year);
    }

    /**
     * Transform a date formatted as MM-dd-yyyy into the format set in the class
     * (default "yyyy-MM-dd'T'HH:mm:ssZ")
     *
     * @param date The date to be formatted
     * @return The string
     */
    public static String getDateRaw(Date date) {
        String rawFmt = instance != null ? instance.format : CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT;
        SimpleDateFormat out = new SimpleDateFormat(rawFmt, Locale.getDefault());
        return out.format(date);
    }

    /**
     * Get today starting time
     * @return APPOINTMENT_DATE_TIME_FORMAT formated date string
     */
    public static String getTodayStart() {
        Calendar cal = Calendar.getInstance(); // locale-specific
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        String rawFmt = instance != null ? instance.format : CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT;
        SimpleDateFormat outDateFormat = new SimpleDateFormat(rawFmt, Locale.getDefault());
        return outDateFormat.format(new Date(cal.getTimeInMillis()));
    }

    /**
     * Get today end time
     * @return APPOINTMENT_DATE_TIME_FORMAT formated date string
     */
    public static String getTodayEnd() {
        Calendar cal = Calendar.getInstance(); // locale-specific
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);
        String rawFmt = CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT;
        SimpleDateFormat outDateFormat = new SimpleDateFormat(rawFmt, Locale.getDefault());
        return outDateFormat.format(new Date(cal.getTimeInMillis()));
    }


    /**
     * Get today end time
     * @return APPOINTMENT_DATE_TIME_FORMAT formated date string
     */
    public static String toDateStringAsYYYYMMDD(Date date) {
        String rawFmt = CarePayConstants.APPOINTMENT_FILTER_DATE_FORMAT;
        SimpleDateFormat outDateFormat = new SimpleDateFormat(rawFmt, Locale.getDefault());
        return outDateFormat.format(date);
    }

    /**
     * Creates a Date object from a string representing a date formatted as MM/dd/yyyy
     *
     * @param dateString The date (as string)
     */
    public static Date parseFromDateAsMMddyyyy(String dateString) {
        SimpleDateFormat formatted = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        Date date = null;
        try {
            date = formatted.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
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

    public String getFormat() {
        return format;
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

    /**
     * Get the ordinal suffix
     *
     * @param number the last digit of the day (as char)
     * @return return a ordinal String with day
     */
    private String getOrdinalSuffix(int number) {
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
     * Verifies if a date has a certain format
     *
     * @param formatString The format string
     * @param dateString The date whose format is to be changed
     * @return The dat in the new format as a string
     */
    public static boolean isValidateStringDate(String dateString, String formatString) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.getDefault());
            format.setLenient(false);
            format.parse(dateString);
        } catch (ParseException e) {
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
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