package com.carecloud.carepaylibray.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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

}
