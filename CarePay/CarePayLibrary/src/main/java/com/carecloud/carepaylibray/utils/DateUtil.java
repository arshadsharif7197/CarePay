package com.carecloud.carepaylibray.utils;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;

/**
 * Created by kkannan on 9/13/16.
 */
public class DateUtil {

    /**
     * get days as a ordinal string
     * @param num the int to evaluate
     * @return return a ordinal String with day
     */
    public static String getDayOrdinal(int num)
    {
        String[] suffix = {"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        int m = num % 100;
        return String.valueOf(num) + suffix[(m > 10 && m < 20) ? 0 : (m % 10)];
    }

    /**
     * convert date string in to month and day; and time into hh:mm am/pm
     *
     * @param dateStr the String to evaluate
     */
    public static String[] onDateParseToString(Context context, String dateStr) {
        String fmt = "yyyy-MM-dd'T'hh:mm:ssZ";
        String formatDate[] = new String[2];
        try {
            // change date format
            SimpleDateFormat inDateFormat = new SimpleDateFormat(fmt, Locale.getDefault());
            SimpleDateFormat outDateFormat = new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault());
            Date date = inDateFormat.parse(dateStr);
            String newDateStr = formatDate[0] = outDateFormat.format(date);
            char lastDayDigit = formatDate[0].charAt(formatDate[0].length() - 1);
            if(lastDayDigit == '1') {
                formatDate[0] = newDateStr + "st";
            } else if(lastDayDigit == '2') {
                formatDate[0] = newDateStr + "nd";
            } else if(lastDayDigit == '3') {
                formatDate[0] = newDateStr + "rd";
            } else {
                formatDate[0] = newDateStr + "th";
            }

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
}
