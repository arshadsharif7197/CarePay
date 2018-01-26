package com.carecloud.carepaylibray.utils;

import android.text.format.DateFormat;
import android.util.Log;

import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.label.Label;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by kkannan on 9/13/16.
 * Helper class to format the date
 *
 * @link (to be assigned)
 */
public class DateUtil {

    public static final String TAG = "DateUtil";

    private static final String FORMAT_TIMEZONE = "yyyy-MM-dd'T'HH:mm:ssZZZZZ";
    private static final String FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String FORMAT_YYYY_DASH_MM_DASH_DD = "yyyy-MM-dd";
    private static final String FORMAT_MM_DASH_DD_DASH_YYYY = "MM-dd-yyyy";
    private static final String FORMAT_MM_SLASH_DD_SLASH_YYYY_EN = "MM/dd/yyyy";
    private static final String FORMAT_MM_SLASH_DD_SLASH_YYYY_ES = "dd/MM/yyyy";
    private static final String FORMAT_HOURS_AM_PM = "h:mm a";
    private static final String FORMAT_MONTH_DAY_TIME12_EN = "MMM dd, h:mm a";
    private static final String FORMAT_MONTH_DAY_TIME12_ES = "dd, MMM. h:mm a";
    private static final String FORMAT_FULL_DATE_TIME12 = "MMM dd, yyyy, h:mm a";
    private static final int IS_A_FUTURE_DATE = 100;
    private static final int IS_A_TOO_OLD_DATE = -100;
    private static final int IS_A_BAD_FORMAT_DATE = -1;
    private static final int IS_A_CORRECT_DATE = 0;
    private static final String FORMAT_ES_DATE_MONTH_LIT = "%s de %s";
    private static final String FORMAT_ES_DATE_MONTH_LIT_YEAR = "%s de %s de %s";
    private static final String FORMAT_ES_DAY_LIT_DATE_MONTH_LIT = "%s %d de %s";


    private static DateUtil instance;
    private String[] formats;
    private Date date;
    private int day;
    private int month; // Attention! 0-indexed month
    private int year;
    private int hour12;
    private int minute;
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

            instance.formats = new String[]{
                    FORMAT_TIMEZONE,
                    FORMAT_ISO_8601,
                    FORMAT_YYYY_DASH_MM_DASH_DD,
                    FORMAT_MM_DASH_DD_DASH_YYYY,
                    FORMAT_MM_SLASH_DD_SLASH_YYYY_EN
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
        if (dateString == null) {
            Log.e(TAG, "Date string is NULL");
            return this.setToCurrent();
        }
        dateString = hackDate(dateString);
        for (String format : formats) {
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
        if (getUserLanguage().equals("en")) {
            //"%s, %s %d%s"
            return String.format(Locale.getDefault(), Label.getLabel("date_day_month_day_ordinal_format"),
                    dayLiteral, monthLiteral, day, getOrdinalSuffix(day));
        } else {
            //"%s %d de %s"
            return String.format(Locale.getDefault(), FORMAT_ES_DAY_LIT_DATE_MONTH_LIT, dayLiteral, day, monthLiteral);
        }
    }

    /**
     * Format the date as "EEEE, MMM d" (eg Monday, Oct 10th)
     *
     * @return A string containing the formatted date
     */
    public String getDateAsDayMonthDayOrdinalYear(String today) {
        if (getUserLanguage().equals("en")) {
            //"%s, %s %d%s"
            return String.format(Locale.getDefault(), Label.getLabel("date_day_month_day_ordinal_format"),
                    this.isToday() ? today : dayLiteral, monthLiteralAbbr, day, getOrdinalSuffix(day));
        } else {
            //"%s %d de %s"
            return String.format(Locale.getDefault(), FORMAT_ES_DAY_LIT_DATE_MONTH_LIT, dayLiteral, day, monthLiteral);
        }
    }

    /**
     * Return the time as 12-hour (format "h:mm a")
     *
     * @return A string contains the formatted time
     */
    public String getTime12Hour() {
        return String.format(Locale.getDefault(), Label.getLabel("date_time_12_hour_format"), //"%d:%02d %s"
                (hour12 == 0) ? 12 : hour12, minute, amPm);
    }

    /**
     * Format current date as Month, Day(ordinal) YYYY (eg October 3rd, 2016)
     *
     * @return The formatted date as string
     */
    public String getDateAsMonthLiteralDayOrdinalYear() {
        String ordinal = instance.getOrdinalSuffix(day); // fetch the ordinal
        if (getUserLanguage().equals("en")) {
            return String.format(Locale.getDefault(), Label.getLabel("date_month_literal_day_ordinal_year_format"),//"%s %s%s, %d"
                    monthLiteral, day, ordinal, year);
        } else {
            //"%s de %s de %s"
            return String.format(Locale.getDefault(), FORMAT_ES_DATE_MONTH_LIT_YEAR, day, monthLiteral, year);
        }
    }

    /**
     * Format current date as MMM, Day(ordinal) YYYY (eg Oct 3rd, 2016)
     *
     * @return The formatted date as string
     */
    public String getDateAsMonthAbbrDayOrdinalYear() {
        String ordinal = instance.getOrdinalSuffix(day); // fetch the ordinal
        if (getUserLanguage().equals("en")) {
            return String.format(Locale.getDefault(), Label.getLabel("date_month_literal_day_ordinal_year_format"),//"%s %s%s, %d"
                    monthLiteralAbbr, day, ordinal, year);
        } else {
            //"%s de %s de %s"
            return String.format(Locale.getDefault(), FORMAT_ES_DATE_MONTH_LIT_YEAR,
                    day, monthLiteralAbbr, year);
        }
    }


    /**
     * Format current date as Month, Day(ordinal) (eg October 3rd)
     *
     * @return The formatted date as string
     */
    public String getDateAsMonthLiteralDayOrdinal() {
        String ordinal = instance.getOrdinalSuffix(day); // fetch the ordinal
        if (getUserLanguage().equals("en")) {
            //"%s %s%s"
            return String.format(Locale.getDefault(), Label.getLabel("date_month_literal_day_ordinal_format"),//"%s %s%s"
                    monthLiteralAbbr, day, ordinal);
        } else {
            //"%s de %s"
            return String.format(Locale.getDefault(), FORMAT_ES_DATE_MONTH_LIT, day, monthLiteralAbbr);
        }
    }

    /**
     * Format current date as Month DD, YYYY, hh:mm a
     *
     * @return formated date as string
     */
    public String getFullDateTime() {
        return toStringWithFormat(FORMAT_FULL_DATE_TIME12);
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
        if (getUserLanguage().equals("en")) {
            return toStringWithFormat(FORMAT_MM_SLASH_DD_SLASH_YYYY_EN);
        } else {
            return toStringWithFormat(FORMAT_MM_SLASH_DD_SLASH_YYYY_ES);
        }
    }

    /**
     * Format date as MM/dd/yyyy
     *
     * @return The formatted date as string
     */
    public String toStringWithFormatDdSlashMmSlashYyyy() {
        return toStringWithFormat(FORMAT_MM_SLASH_DD_SLASH_YYYY_ES);
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

    /**
     * @param date as Date
     * @return The current DateUtil object
     */
    public DateUtil setDate(Date date) {
        this.date = date;
        updateFields();

        return this;
    }

    /**
     * @param calendar as Date
     * @return The current DateUtil object
     */
    public DateUtil setDate(Calendar calendar) {
        this.date = calendar.getTime();
        updateFields(calendar);

        return this;
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
                return Label.getLabel("ordinal_th");
            } else {
                return Label.getLabel("ordinal_st"); // if end in 1, use 'st'
            }
        } else if (dayLastDigit == 2) {
            if (lastTwoDigits == 12) { // if it ends in 12, use 'th'}
                return Label.getLabel("ordinal_th");
            } else { // if ends just in 2, use "nd"
                return Label.getLabel("ordinal_nd");
            }
        } else if (dayLastDigit == 3) {
            if (lastTwoDigits == 13) {
                return Label.getLabel("ordinal_th"); // if last two digits are 13, use 'th'
            } else {
                return Label.getLabel("ordinal_rd"); // else use 'rd'
            }
        } else {
            return Label.getLabel("ordinal_th");
        }
    }

    private void updateFields() {
        if (date != null) {
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.setTime(date);
            updateFields(calendar);
        }
    }

    private void updateFields(Calendar calendar) {
        String languageTag = getUserLanguage();
        if (languageTag.equals("es")) {
            Locale spanish = new Locale("es", "ES");
            Locale.setDefault(spanish);
        } else {
            Locale.setDefault(Locale.US);
        }
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

    private String getUserLanguage() {
        return ApplicationPreferences.getInstance().getUserLanguage();
    }

    /**
     * @param days to add
     * @return updated DateUtil
     */
    public DateUtil addDays(int days) {
        if (null != date) {
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_YEAR, days);
            date = calendar.getTime();
            updateFields();
        }

        return this;
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
     * Check whether the provided day is today
     *
     * @param date Date to check
     * @return true if date is today
     */
    public static boolean isToday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return isToday(calendar);
    }

    /**
     * Check whether the provided day is today
     *
     * @param calendar Calendar to check
     * @return true if calendar is today
     */
    public static boolean isToday(Calendar calendar) {
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

        return crtDay == day - 1 && crtMonth == month && crtYear == year;
    }

    /**
     * Check whether the provided date is tomorrow
     *
     * @param date Date to check
     * @return true if Date is tomorrow
     */
    public static boolean isTomorrow(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return isTomorrow(calendar);
    }

    /**
     * Check whether the provided day is tomorrow
     *
     * @param calendar Calendar to check
     * @return true if Calendar day is tomorrow
     */
    public static boolean isTomorrow(Calendar calendar) {
        Calendar checkCal = Calendar.getInstance();
        checkCal.add(Calendar.DAY_OF_YEAR, 1);

        return isSameDay(calendar, checkCal);

    }

    /**
     * Check whether the provided day corresponds to the last day of the current month
     *
     * @param calendar Calendar to check
     * @return true if day is first of the month
     */
    public static boolean startsThisMonth(Calendar calendar) {
        Calendar checkCal = Calendar.getInstance();
        checkCal.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));

        return isSameDay(calendar, checkCal);
    }

    /**
     * Check if given date corresponds to last day in month
     *
     * @return true if date is last day in month
     */
    public boolean endsThisMonth() {

        return endsThisMonth(date);
    }


    /**
     * Check whether the provided date corresponds to the last day of the current month
     *
     * @param date Date to check
     * @return true if date is last of the month
     */
    public static boolean endsThisMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return endsThisMonth(calendar);
    }

    /**
     * Check whether the provided day corresponds to the last day of the current month
     *
     * @param calendar Calendar to check
     * @return true if day is last of the month
     */
    public static boolean endsThisMonth(Calendar calendar) {
        Calendar checkCal = Calendar.getInstance();
        int calMaxDayMonth = checkCal.getActualMaximum(Calendar.DAY_OF_MONTH);
        checkCal.set(Calendar.DAY_OF_MONTH, calMaxDayMonth);

        return isSameDay(calendar, checkCal);
    }

    /**
     * Get the number of days elapsed between two dates
     *
     * @param start starting date
     * @param end   ending date
     * @return number of days elapsed
     */
    public static int getDaysElapsed(Date start, Date end) {
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();

        startCal.setTime(start);
        endCal.setTime(end);

        return getDaysElapsed(startCal, endCal);
    }

    /**
     * Get the number of days elapsed between two Calendar days
     *
     * @param start starting day
     * @param end   ending day
     * @return number of days elapsed
     */
    public static int getDaysElapsed(Calendar start, Calendar end) {
        if (end.compareTo(start) < 0) {//parameters in wrong order
            Log.w(TAG, "calendar parameters out of order");
            Calendar temp = start;
            start = end;
            end = temp;
        }

        int years = end.get(Calendar.YEAR) - start.get(Calendar.YEAR);
        if (years == 0) {//is the time period within the current year range
            return end.get(Calendar.DAY_OF_YEAR) - start.get(Calendar.DAY_OF_YEAR);
        } else if (years == 1) {//this is probably a care where the range is carrying over at the end of the year
            int daysLeftStartingYear = start.getActualMaximum(Calendar.DAY_OF_YEAR) - start.get(Calendar.DAY_OF_YEAR);
            return daysLeftStartingYear + end.get(Calendar.DAY_OF_YEAR);
        } else {//need to figure out how many years we are looking at
            int fullYearCount = years - 1;

            int leapYearOffset = 0;
            int yearCheck = start.get(Calendar.YEAR);
            for (int i = 0; i < fullYearCount; i++) {
                yearCheck++;
                if (yearCheck % 4 == 0) {
                    leapYearOffset++;
                }
            }

            int daysLeftStartingYear = start.getActualMaximum(Calendar.DAY_OF_YEAR) - start.get(Calendar.DAY_OF_YEAR);
            return daysLeftStartingYear + fullYearCount * 365 + end.get(Calendar.DAY_OF_YEAR) + leapYearOffset;
        }
    }

    /**
     * Like {@link #getDaysElapsed(Date, Date)} but includes the current date in the count
     *
     * @param start starting date
     * @param end   ending date
     * @return number of days elapsed including the starting date
     */
    public static int getDaysElapsedInclusive(Date start, Date end) {
        return getDaysElapsed(start, end) + 1;
    }

    /**
     * Like {@link #getDaysElapsed(Calendar, Calendar)} but includes the current date in the count
     *
     * @param start starting day
     * @param end   ending day
     * @return number of days elapsed including the starting day
     */
    public static int getDaysElapsedInclusive(Calendar start, Calendar end) {
        return getDaysElapsed(start, end) + 1;
    }

    /**
     * Get the number of hours elapsed between two Dates
     *
     * @param start start date
     * @param end   end date
     * @return hours elapsed
     */
    public static long getHoursElapsed(Date start, Date end) {
        long differenceInMilli = Math.abs(start.getTime() - end.getTime());

        return TimeUnit.MILLISECONDS.toHours(differenceInMilli);
    }


    /**
     * Get the number of months elapsed between two Dates
     *
     * @param start start Date
     * @param end   end Date
     * @return number of months elapsed
     */
    public static int getMonthsElapsed(Date start, Date end) {
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();

        startCal.setTime(start);
        endCal.setTime(end);

        return getMonthsElapsed(startCal, endCal);
    }

    /**
     * Get the number of months elapsed between two Calendar instances
     *
     * @param start start Calendar
     * @param end   end Calendar
     * @return number of months elapsed
     */
    public static int getMonthsElapsed(Calendar start, Calendar end) {
        if (end.compareTo(start) < 0) {//parameters in wrong order
            Log.w(TAG, "calendar parameters out of order");
            Calendar temp = start;
            start = end;
            end = temp;
        }

        int years = end.get(Calendar.YEAR) - start.get(Calendar.YEAR);
        if (years == 0) {//is the time period within the current year range
            return end.get(Calendar.MONTH) - start.get(Calendar.MONTH);
        } else if (years == 1) {//this is probably a case where the range is carrying over at the end of the year
            int monthsRemainingStartYear = start.getActualMaximum(Calendar.MONTH) - start.get(Calendar.MONTH);
            return monthsRemainingStartYear + end.get(Calendar.MONTH);
        } else {//need to figure out how many years we are looking at
            int fullYearCount = years - 1;

            int monthsRemainingStartYear = start.getActualMaximum(Calendar.MONTH) - start.get(Calendar.MONTH);
            return monthsRemainingStartYear + fullYearCount * start.getActualMaximum(Calendar.MONTH) + end.get(Calendar.MONTH);
        }

    }

    /**
     * Get the number of years elapsed between two Dates
     *
     * @param start start Date
     * @param end   end Date
     * @return number of years elapsed
     */
    public static int getYearsElapsed(Date start, Date end) {
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();

        startCal.setTime(start);
        endCal.setTime(end);

        return getYearsElapsed(startCal, endCal);
    }

    /**
     * Get the number of years elapsed between two Calendar Instances
     *
     * @param start start Calendar
     * @param end   end Calendar
     * @return number of years elapsed
     */
    public static int getYearsElapsed(Calendar start, Calendar end) {
        if (end.compareTo(start) < 0) {//parameters in wrong order
            Log.w(TAG, "calendar parameters out of order");
            Calendar temp = start;
            start = end;
            end = temp;
        }

        return end.get(Calendar.YEAR) - start.get(Calendar.YEAR);
    }

    /**
     * Check whether the provided days are in the same year
     *
     * @param start starting day
     * @param end   ending day
     * @return true if days are in same year
     */
    public static boolean isSameYear(Calendar start, Calendar end) {
        return start.get(Calendar.YEAR) == end.get(Calendar.YEAR);
    }

    /**
     * Check whether the provided dates are in the same year
     *
     * @param start starting date
     * @param end   ending date
     * @return true if dates are in same year
     */
    public static boolean isSameYear(Date start, Date end) {
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();

        startCal.setTime(start);
        endCal.setTime(end);

        return isSameYear(startCal, endCal);
    }


    /**
     * Check whether the provided days are the same day regardless of time
     *
     * @param start starting day
     * @param end   endind day
     * @return true if same day
     */
    public static boolean isSameDay(Calendar start, Calendar end) {
        return isSameYear(start, end) && start.get(Calendar.DAY_OF_YEAR) == end.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Check whether the provided dates are the same day regardless of time
     *
     * @param start starting date
     * @param end   ending date
     * @return true if same day
     */
    public static boolean isSameDay(Date start, Date end) {
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
    public static int validateDateOfBirth(String dateString) {
        String formatString = "MM/dd/yyyy";
        try {
            SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.getDefault());
            format.setLenient(false);
            Date dob = format.parse(dateString);
            getInstance().setDate(dob);
            if (!getInstance().isYesterdayOrBefore()) {
                return IS_A_FUTURE_DATE;
            }
            if (getInstance().year < 1900) {
                return IS_A_TOO_OLD_DATE;
            }
            return IS_A_CORRECT_DATE;
        } catch (ParseException e) {
            return IS_A_BAD_FORMAT_DATE;
        } catch (IllegalArgumentException e) {
            return IS_A_CORRECT_DATE;
        }
    }

    /**
     * @param dateString the date to be validated
     * @return a readable error message from the result of validating a date
     */
    public static String getDateOfBirthValidationResultMessage(String dateString) {
        if (dateString != null) {
            int resultCode = validateDateOfBirth(dateString);
            if (resultCode == DateUtil.IS_A_BAD_FORMAT_DATE) {
                return Label.getLabel("demographics_date_bad_format_msg");
            } else if (resultCode == DateUtil.IS_A_FUTURE_DATE) {
                return Label.getLabel("demographics_date_validation_msg");
            } else if (resultCode == DateUtil.IS_A_TOO_OLD_DATE) {
                return Label.getLabel("demographics_date_old_msg");
            }
        }
        return null;

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
        if (getUserLanguage().equals("en")) {
            //"%s, %s %d%s"
            return String.format(Locale.getDefault(), Label.getLabel("date_day_month_day_ordinal_format"),
                    dayLiteral, monthLiteralAbbr, day, getOrdinalSuffix(day));
        } else {
            //"%s %d de %s"
            return String.format(Locale.getDefault(), FORMAT_ES_DAY_LIT_DATE_MONTH_LIT, dayLiteral, day, monthLiteral);
        }
    }

    /**
     * Convinience method for formatting a date range using contextual output
     *
     * @param startDate start of date range
     * @param endDate   end of date range
     * @param today     String to represent today output
     * @param tomorrow  String to represent tomorrow output
     * @param thisMonth String to represent this month as output
     * @param nextDays  Formatted String to represent upcoming day count
     * @return Contextually formatted Date range
     */
    public static String getFormattedDate(Date startDate, Date endDate, String today,
                                          String tomorrow, String thisMonth, String nextDays) {
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();

        startCal.setTime(startDate);
        endCal.setTime(endDate);

        String fromText = getFormattedDate(startDate, today, tomorrow);

        //check for single Day
        if (isSameDay(startCal, endCal)) {
            return fromText;
        }

        // Check for whole month
        if (startsThisMonth(startCal) && endsThisMonth(endCal)) {
            return thisMonth;
        }

        int elapsedDays = getDaysElapsedInclusive(startCal, endCal);

        //check if the range starts today
        if (isToday(startCal) && elapsedDays < 32) {
            return String.format(nextDays, elapsedDays);
        }

        String toText = getFormattedShortDate(endDate, today, tomorrow);
        fromText = getFormattedShortDate(startDate, today, tomorrow);

        //return from - to format
        return String.format("%s - %s", fromText, toText);
    }

    /**
     * Convinience method for formatting a date range using contextual output
     *
     * @param today    String to represent today output
     * @param tomorrow String to represent tomorrow output
     * @return Contextually formatted Date range
     */
    public static String getFormattedDate(Date date, String today, String tomorrow) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        //check for today
        if (isToday(calendar)) {
            return today;
        }

        //check for tomorrow
        if (isTomorrow(calendar)) {
            return tomorrow;
        }

        //Just return this date in readable format
        return getInstance().setDate(date).getDateAsDayMonthDayOrdinal();
    }

    /**
     * Convinience method for formatting a date range using contextual output that returns a short date
     *
     * @param date     date to format
     * @param today    String to represent today output
     * @param tomorrow String to represent tomorrow output
     * @return Contextually formatted Date range in short format mm/dd/yyyy
     */
    public static String getFormattedShortDate(Date date, String today, String tomorrow) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        //check for today
        if (isToday(calendar)) {
            return today;
        }

        //check for tomorrow
        if (isTomorrow(calendar)) {
            return tomorrow;
        }

        //Just return this date in readable format
        return getInstance().setDate(date).toStringWithFormatMmSlashDdSlashYyyy();
    }

    /**
     * @param date1 date 1
     * @param date2 date 2
     * @return minutes ellapsed
     */
    public static long getMinutesElapsed(Date date1, Date date2) {
        long differenceInMilli = Math.abs(date1.getTime() - date2.getTime());

        return TimeUnit.MILLISECONDS.toMinutes(differenceInMilli);
    }


    public static long getSecondsElapsed(Date date1, Date date2) {
        long differenceInMilli = Math.abs(date1.getTime() - date2.getTime());

        return TimeUnit.MILLISECONDS.toSeconds(differenceInMilli);
    }

    /**
     * Get readable time elapsed between two dates in format: hh:MM:SS
     *
     * @param date1 date
     * @param date2 date
     * @return readable string;
     */
    public static String getTimeElapsed(Date date1, Date date2) {
        long differenceInMilli = Math.abs(date1.getTime() - date2.getTime());

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;

        final long elapsedHours = differenceInMilli / hoursInMilli;
        differenceInMilli = differenceInMilli - (elapsedHours * hoursInMilli);

        final long elapsedMinutes = differenceInMilli / minutesInMilli;
        differenceInMilli = differenceInMilli - (elapsedMinutes * minutesInMilli);

        final long elapsedSeconds = differenceInMilli / secondsInMilli;

        StringBuilder builder = new StringBuilder();
        if (elapsedHours > 0) {
            builder.append(elapsedHours);
            builder.append(':');
        }
        if (elapsedMinutes < 10) {
            builder.append('0');
            builder.append(elapsedMinutes);
        } else {
            builder.append(elapsedMinutes);
        }
        builder.append(':');
        if (elapsedSeconds < 10) {
            builder.append('0');
        }
        builder.append(elapsedSeconds);
        return builder.toString();
    }

    /**
     * @param date the date
     * @return returns a string formatted like 10:00 PM
     */
    public static String getHoursFormatted(Date date) {
        return DateFormat.format(FORMAT_HOURS_AM_PM, date).toString();
    }

    /**
     * @param rawDate a string containing the date
     * @return returns a string formatted like 10:00 PM
     */
    public static String getHoursFormatted(String rawDate) {
        Date date = getInstance().setDateRaw(rawDate).getDate();
        return DateFormat.format(FORMAT_HOURS_AM_PM, date).toString();
    }

    public static String getContextualTimeElapsed(Date date1, Date date2) {
        long hoursElapsed = getHoursElapsed(date1, date2);
        if (hoursElapsed > 0) {
            if (hoursElapsed == 1) {
                return hoursElapsed + Label.getLabel("label_hours_ago_singular");
            }
            return hoursElapsed + Label.getLabel("label_hours_ago");
        }
        long minutesElapsed = getMinutesElapsed(date1, date2);
        if (minutesElapsed > 0) {
            if (minutesElapsed == 1) {
                return minutesElapsed + Label.getLabel("label_minutes_ago_singular");
            }
            return minutesElapsed + Label.getLabel("label_minutes_ago");
        }
        long secondsElapsed = getSecondsElapsed(date1, date2);
        if (secondsElapsed == 1) {
            return secondsElapsed + Label.getLabel("label_seconds_ago_singular");
        }
        return secondsElapsed + Label.getLabel("label_seconds_ago");
    }

    /**
     * Get contextual date for messaging
     *
     * @return contextual date string
     */
    public String toContextualMessageDate() {
        Date compareDate = new Date();
        if (isToday()) {
            return getTime12Hour();
        } else {
            int daysElapsed = getDaysElapsed(getDate(), compareDate);
            if (daysElapsed < 7) {
                if (daysElapsed == 1) {
                    return Label.getLabel("label_yesterday");
                }
                return String.format(Label.getLabel("label_days_ago"), daysElapsed);//"%s days ago"
            } else if (daysElapsed < 28) {
                int weeksElapsed = daysElapsed / 7;
                if (weeksElapsed == 1) {
                    return Label.getLabel("label_last_week");
                } else {
                    return String.format(Label.getLabel("label_weeks_ago"), weeksElapsed);//"%s weeks ago"
                }
            } else if (isSameYear(getDate(), compareDate)) {
                int monthsElapsed = getMonthsElapsed(getDate(), compareDate);
                if (monthsElapsed == 1) {
                    return Label.getLabel("label_last_month");
                } else {
                    return String.format(Label.getLabel("label_months_ago"), monthsElapsed);//"%s months ago"
                }
            } else {
                int yearsElapsed = getYearsElapsed(getDate(), compareDate);
                if (yearsElapsed == 1) {
                    return Label.getLabel("label_last_year");
                } else {
                    return String.format(Label.getLabel("label_years_ago"), yearsElapsed);//"%s years ago"
                }
            }
        }
    }

    /**
     * Shift current date to GMT by copying current date & time fields to new GMT time zone calendar.
     * This will cause a new date to be set shifted to the GMT time zone but maintaining the current time values
     * 1/1/2001 13:00:00 EST will become 1/1/2001 13:00:00 GMT
     *
     * @return DateUtil instance with updated Date value
     */
    public DateUtil shiftDateToGMT() {
        if (date == null) {
            date = new Date();
        }
        Calendar calLocal = Calendar.getInstance();
        Calendar calGMT = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

        calLocal.setTime(getDate());
        calGMT.set(calLocal.get(Calendar.YEAR), calLocal.get(Calendar.MONTH), calLocal.get(Calendar.DATE), calLocal.get(Calendar.HOUR_OF_DAY), calLocal.get(Calendar.MINUTE));
        calGMT.getTime();
        calGMT.setTimeZone(calLocal.getTimeZone());

        setDate(calGMT);
        return this;
    }

    public String getDateAsMonthDayTime() {
        if (getUserLanguage().equals("en")) {
            return toStringWithFormat(FORMAT_MONTH_DAY_TIME12_EN);
        } else {
            return toStringWithFormat(FORMAT_MONTH_DAY_TIME12_ES);
        }
    }

    private String hackDate(String dateString) {
        return dateString.replaceAll("\\.\\d\\d\\dZ", "-00:00");
    }

    public String getDateAsMonthDayYear() {
        if (getUserLanguage().equals("en")) {
            return toStringWithFormatMmSlashDdSlashYyyy();
        } else {
            return toStringWithFormatDdSlashMmSlashYyyy();
        }
    }
}