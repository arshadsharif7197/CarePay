package com.carecloud.carepaylibray.utils;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.google.api.client.util.DateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;

/**
 * Created by kkannan on 9/13/16.
 */
public class DateUtil {

    /**
     * get days as a ordinal string
     * @param dayLastDigit the last digit of the day (as char)
     * @return return a ordinal String with day
     */
    public static String getDayOrdinal(char dayLastDigit)
    {
        if(dayLastDigit == '1') {
             return "st";
        } else if(dayLastDigit == '2') {
            return "nd";
        } else if(dayLastDigit == '3') {
            return "rd";
        } else {
            return "th";
        }
    }

    /**
     * convert date string in to month and day; and time into hh:mm am/pm
     *
     * @param dateStr the String to evaluate
     */
    public static String[] parseStringToDateTime(Context context, String dateStr) {
        String fmt = "yyyy-MM-dd'T'hh:mm:ssZ";
        String formatDate[] = new String[2];
        try {
            // change date format
            SimpleDateFormat inDateFormat = new SimpleDateFormat(fmt, Locale.getDefault());
            SimpleDateFormat outDateFormat = new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault());
            Date date = inDateFormat.parse(dateStr);
            String newDateStr = formatDate[0] = outDateFormat.format(date);
            char lastDayDigit = formatDate[0].charAt(formatDate[0].length() - 1);
            formatDate[0] = newDateStr + getDayOrdinal(lastDayDigit);

            // change time format
            SimpleDateFormat inTimeFormat = new SimpleDateFormat(fmt, Locale.getDefault());
            Date time = inTimeFormat.parse(dateStr);
            SimpleDateFormat outTimeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            formatDate[1] = outTimeFormat.format(time);
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage());
        }
        return formatDate;
    }

    /** .
     *
     * @return returns formatted date
     */
    public static String formatCurrentDateAsMonthDayYear() {
        // Create a calendar object that will convert the date
        // and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        String monthString = getMonthAsString(month);
        String dayString = String.valueOf(day);
        String ordinal = getDayOrdinal(dayString.charAt(dayString.length() - 1));

        return String.format(Locale.getDefault(), "%s, %s%s %d",
                monthString, dayString, ordinal, year);
    }

    private static String getMonthAsString(int month) {
        switch(month) {
            case Calendar.JANUARY: return "January";
            case Calendar.FEBRUARY: return "February";
            case Calendar.MARCH: return "March";
            case Calendar.APRIL: return "April";
            case Calendar.MAY: return "May";
            case Calendar.JUNE: return "June";
            case Calendar.JULY: return "July";
            case Calendar.AUGUST: return "August";
            case Calendar.SEPTEMBER: return "September";
            case Calendar.OCTOBER: return "October";
            case Calendar.NOVEMBER: return "November";
            case Calendar.DECEMBER: return "December";
        }
        return null;
    }
}
