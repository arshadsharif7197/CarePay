package com.carecloud.carepaylibray.utils;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.google.api.client.util.DateTime;

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
    private        Date     date;
    private static DateUtil instance;

    public static DateUtil getInstance() {
        if (instance == null) {
            instance = new DateUtil();
            instance.date = Calendar.getInstance().getTime(); // by default set the date to current
        }
        return instance;
    }

    private DateUtil() {
        this.date = new Date();
    }

    public DateUtil setDateRaw(String dateString) {
        try {
            if (instance != null) {
                SimpleDateFormat formatter = new SimpleDateFormat(instance.format, Locale.getDefault());
                instance.setDate(formatter.parse(dateString));
            }
        } catch (ParseException e) {
            Log.v(LOG_TAG, DateUtil.class.getSimpleName() + " exception ", e);
        }
        return instance;
    }

    public static DateUtil setFormat(String format) {
        if(instance != null) {
            instance.format = format;
        }
        return instance;
    }

    private void setDate(Date date) {
        this.date = date;
    }

    private Date getDate() {
        return date;
    }

    /**
     * get days as a ordinal string
     *
     * @param dayLastDigit the last digit of the day (as char)
     * @return return a ordinal String with day
     */
    private String getDayOrdinal(char dayLastDigit) {
        if (dayLastDigit == '1') {
            return "st";
        } else if (dayLastDigit == '2') {
            return "nd";
        } else if (dayLastDigit == '3') {
            return "rd";
        } else {
            return "th";
        }
    }

    /**
     * Format the date as DayOfWeek, Month day th (eg Monday, October 10th)
     * @return A string containing the formatted date
     */
    public String getDateAsDayMonthDayOrdinal() {
        // create a formatter
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault());
        // format the date as Day_of_week, Month day th
        String newDateString =  formatter.format(getDate());
        // add the ordinal return
        char lastDigit = newDateString.charAt(newDateString.length() - 1);
        return newDateString  + getDayOrdinal(lastDigit);
    }

    /**
     * Return the time as 12-hour
     * @return A string contains the formatted time
     */
    public String getTime12Hour() {
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a", Locale.getDefault());
        return formatter.format(date);
    }

    /**
     * Format current date as Month, Day(ordinal) YYYY ()
     * @return The formatted date as string
     */
    public String formatCurrentDateAsMonthDayYear() {
        // Create a calendar object that will convert the date
        // and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        String monthString = instance.getMonthAsString(month);
        String dayString = String.valueOf(day);
        String ordinal = instance.getDayOrdinal(dayString.charAt(dayString.length() - 1));

        return String.format(Locale.getDefault(), "%s %s%s, %d",
                             monthString, dayString, ordinal, year);
    }

    private String getMonthAsString(int month) {
        switch (month) {
            case Calendar.JANUARY:
                return "January";
            case Calendar.FEBRUARY:
                return "February";
            case Calendar.MARCH:
                return "March";
            case Calendar.APRIL:
                return "April";
            case Calendar.MAY:
                return "May";
            case Calendar.JUNE:
                return "June";
            case Calendar.JULY:
                return "July";
            case Calendar.AUGUST:
                return "August";
            case Calendar.SEPTEMBER:
                return "September";
            case Calendar.OCTOBER:
                return "October";
            case Calendar.NOVEMBER:
                return "November";
            case Calendar.DECEMBER:
                return "December";
        }
        return null;
    }

    /**
     * Format dat as mm/dd/yyyy
     * @return The formatted date as string
     */
    public String formatToDateOfBirth() {
//        String dobFormat = context.getString(R.string.dateFormatString);
        String dobFormat = "MM-dd-yyyy";
        SimpleDateFormat formatter = new SimpleDateFormat(dobFormat, Locale.getDefault());
        return formatter.format(date);
    }

    public String convertToRawFromDateOfBirthFormat(String dateOfBirth) {
        SimpleDateFormat in = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        Date date = null;
        try {
            date = in.parse(dateOfBirth);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "convertToRawFromDateOfBirthFormat() ", e);
        }
        SimpleDateFormat out = new SimpleDateFormat(instance.format, Locale.getDefault());

        return out.format(date);
    }
}
