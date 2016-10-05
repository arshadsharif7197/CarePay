package com.carecloud.carepaylibray.utils;

import android.util.Log;

import com.carecloud.carepaylibray.consentforms.interfaces.FormData;
import com.carecloud.carepaylibray.constants.CarePayConstants;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;

/**
 * Created by kkannan on 9/13/16.
 * Helper class to format the date
 *
 * @link (to be assigned)
 */
public class DateUtil {

    private String format = CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT;
    private static DateUtil instance;
    private        Date     date;
    private        int      day;
    private        int      month; // Attention! 0-indexed month
    private        int      year;
    private        int      hour12;
    private        int      minute;
    private        String   dayLiteral;
    private        String   monthLiteral;
    private        String   dayLiteralAbbr;
    private        String   monthLiteralAbbr;
    private        String   amPm;

    /**
     * Returns the instance of this singleton
     *
     * @return The instance
     */
    public static DateUtil getInstance() {
        if (instance == null) {
            instance = new DateUtil();
            instance.setDate(Calendar.getInstance(Locale.getDefault()).getTime()); // by default set the date to current date
        }
        return instance;
    }

    /**
     * Set the a date to be formated;
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
//            Log.e(LOG_TAG, DateUtil.class.getSimpleName() + " exception ", e);
        }
        return this;
    }

    /**
     * Reset the current reference to the current date
     * @return The instance
     */
    public DateUtil setToCurrent() {
        date = Calendar.getInstance().getTime();
        updateFields();
        return this;
    }

    /**
     * Set the format of the in-coming date
     * By default this is "yyyy-MM-dd'T'HH:mm:ssZ".
     *
     * @param format The new format
     */
    public static void setFormat(String format) {
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
     * Return the time as 12-hour (format "h:mm a")
     *
     * @return A string contains the formatted time
     */
    public String getTime12Hour() {
        return String.format(Locale.getDefault(), "%2d:%02d %s", hour12, minute, amPm);
    }

    /**
     * Format current date as Month, Day(ordinal) YYYY ()
     *
     * @return The formatted date as string
     */
    public String getCrtDateAsMonthLiteralDayOrdinalYear() {
        String ordinal = instance.getOrdinalSuffix(day); // fetch the ordinal
        return String.format(Locale.getDefault(), "%s %s%s, %d",
                             monthLiteral, dayLiteral, ordinal, year);
    }

    /**
     * Format dat as MM-dd-yyyy
     *
     * @return The formatted date as string
     */
    public String getDateAsMMddyyyy() {
        return String.format(Locale.getDefault(), "%02d-%02d-%4d", month + 1, day, year);
    }

    /**
     * Transform a date formatted as MM-dd-yyyy into the format set in the class
     * (default "yyyy-MM-dd'T'HH:mm:ssZ")
     *
     * @param dateString dateString
     * @return The string
     */
    public static String getDateRaw(Date date) {
        SimpleDateFormat out = new SimpleDateFormat(instance.format, Locale.getDefault());
        return out.format(date);
    }

    /**
     * Creates a date from a string formatted as MM-dd-yyyy
     * @param dateString The date (as string)
     */
    public static Date parseFromDateAsMMddyyyy(String dateString) {
        SimpleDateFormat formatted = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        Date date = null;
        try {
            date = formatted.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
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
     * @return The date
     */
    public Date getDate() {
        return date;
    }

    private DateUtil() {
        this.date = new Date();
    }

    private void setDate(Date date) {
        this.date = date;
        updateFields();
    }

    /**
     * Get the ordinal suffix
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
            if(lastTwoDigits == 13) {
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

}