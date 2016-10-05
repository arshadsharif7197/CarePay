package com.carecloud.carepaylibray.utils;

import android.content.Context;
import android.util.Log;

import com.carecloud.carepaylibray.constants.CarePayConstants;

import java.text.ParseException;
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
    public static String[] parseStringToDateTime(String dateStr) {
        String fmt = CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT;

        String formatDate[] = new String[2];
        try {
            // change date format
            SimpleDateFormat inDateFormat = new SimpleDateFormat(CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT, Locale.getDefault());
            SimpleDateFormat outDateFormat = new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault());
            Date date = inDateFormat.parse(dateStr);

            String newDateStr = formatDate[0] = outDateFormat.format(date);
            char lastDayDigit = formatDate[0].charAt(formatDate[0].length() - 1);
            formatDate[0] = newDateStr + getDayOrdinal(lastDayDigit);

            // change time format
            SimpleDateFormat inTimeFormat = new SimpleDateFormat(CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT, Locale.getDefault());
            Date time = inTimeFormat.parse(dateStr);
            SimpleDateFormat outTimeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            formatDate[1] = outTimeFormat.format(time);
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage());
        }
        return formatDate;
    }

    /**
     * Format current date as Month, Day(ordinal) YYYY
     * @return The formatted date as string
     */
    public static String formatCurrentDateAsMonthDayYear() {
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        String monthString = getMonthAsString(month);
        String dayString = String.valueOf(day);
        String ordinal = getDayOrdinal(dayString.charAt(dayString.length() - 1));

        return String.format(Locale.getDefault(), "%s, %s%s %d", monthString, dayString, ordinal, year);
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

    /**
     * Format dat as mm/dd/yyyy
     * @param context The context
     * @param date The date
     * @return The formatted date as string
     */
    public static String formatToDateOfBirth(Context context, Date date) {
//        String dobFormat = context.getString(R.string.dateFormatString);
        String dobFormat = "MM-dd-yyyy";
        SimpleDateFormat formatter = new SimpleDateFormat(dobFormat, Locale.getDefault());
        return formatter.format(date);
    }


    public static Date getDateInRawFormatFromString(String datetime) {
        // TODO: 10/3/2016 make it work for the general format
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        try {
            return formatter.parse(datetime);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Date util parse error", e);
        }
        return null;
    }

    public static String convertToRawFromDateOfBirthFormat(String dateOfBirth) {
        SimpleDateFormat in = new SimpleDateFormat("yyyy/mm/dd", Locale.getDefault());
        Date date = null;
        try {
            date = in.parse(dateOfBirth);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "convertToRawFromDateOfBirthFormat() ", e);
        }
        SimpleDateFormat out = new SimpleDateFormat(CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT, Locale.getDefault());

        return out.format(date);
    }

    public static Date parseStringToDate(String dateStr) {
        // Get appointment date/time in required format
        try {
            SimpleDateFormat format = new SimpleDateFormat(
                    CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT, Locale.getDefault());
            return format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String parseDateToString(Date date) {
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(
                CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT, Locale.getDefault());
        return mSimpleDateFormat.format(date);
    }

    public static Date parseStringToTime(String dateStr) {
        // Get appointment date/time in required format
        try {
            SimpleDateFormat format = new SimpleDateFormat(
                    CarePayConstants.TIME_FORMAT_AM_PM, Locale.getDefault());
            return format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String parseTimeToString(Date date) {
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(
                CarePayConstants.TIME_FORMAT_AM_PM, Locale.getDefault());
        return mSimpleDateFormat.format(date);
    }

    public static String parseDateToString(String format, Date date) {
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
        return mSimpleDateFormat.format(date);
    }

    public static Date parseDateToString(String format, String dateStr) {
        // Get appointment date/time in required format
        try {
            SimpleDateFormat newFormat = new SimpleDateFormat(format, Locale.getDefault());
            return newFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
