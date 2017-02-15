package com.carecloud.carepay.patient;

import com.carecloud.carepaylibray.utils.DateUtil;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;




/**
 * Created by lsoco_user on 10/5/2016.
 * Tests for the DateUtil
 */
public class DateUtilTest {

    final private String[] weekDays = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    final private String[] weekDaysAbbrs = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    final private String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    final private String[] monthsAbbr = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    final private String[] amPm = {"AM", "PM"};
    final private String rawDate = "2016-10-03T16:30:00-04:00";

    @Test
    public void hasDefaultAllFieldsValid() {
        // test current date
        assertThat("invalid current day", DateUtil.getInstance().getDay(), is(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)));
        assertThat("invalid current month", DateUtil.getInstance().getMonth(), is(Calendar.getInstance().get(Calendar.MONTH)));
        assertThat("invalid current year", DateUtil.getInstance().getYear(), is(Calendar.getInstance().get(Calendar.YEAR)));
        assertThat("invalid current day literal", DateUtil.getInstance().getDayLiteral(), is(weekDays[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1]));
        assertThat("invalid current month literal", DateUtil.getInstance().getMonthLiteral(), is(months[Calendar.getInstance().get(Calendar.MONTH)]));
        assertThat("invalid current day abbr", DateUtil.getInstance().getDayLiteralAbbr(), is(weekDaysAbbrs[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1]));
        assertThat("invalid current month abbr", DateUtil.getInstance().getMonthLiteralAbbr(), is(monthsAbbr[Calendar.getInstance().get(Calendar.MONTH)]));
        assertThat("invalid current am or pm", DateUtil.getInstance().getAmPm(), is(amPm[Calendar.getInstance().get(Calendar.AM_PM)]));
        assertThat("invalid current hour", DateUtil.getInstance().getHour12(), is(Calendar.getInstance().get(Calendar.HOUR)));
        assertThat("invalid current minute", DateUtil.getInstance().getMinute(), is(Calendar.getInstance().get(Calendar.MINUTE)));
    }

    @Test
    public void hasSetDateAllFieldsValid() {
        // test set date

        DateUtil.getInstance().setDateRaw(rawDate);

        assertThat("invalid day", DateUtil.getInstance().getDay(), is(3));
        assertThat("invalid month", DateUtil.getInstance().getMonth(), is(Calendar.OCTOBER));
        assertThat("invalid year", DateUtil.getInstance().getYear(), is(2016));
        assertThat("invalid day literal", DateUtil.getInstance().getDayLiteral(), is("Monday"));
        assertThat("invalid month literal", DateUtil.getInstance().getMonthLiteral(), is("October"));
        assertThat("invalid day abbr", DateUtil.getInstance().getDayLiteralAbbr(), is("Mon"));
        assertThat("invalid month abbr", DateUtil.getInstance().getMonthLiteralAbbr(), is("Oct"));
        assertThat("invalid am or pm", DateUtil.getInstance().getAmPm(), is("PM"));
        assertThat("invalid hour", DateUtil.getInstance().getHour12(), is(4));
        assertThat("invalid minute", DateUtil.getInstance().getMinute(), is(30));
    }

    @Test
    public void doesResetTheDate() {
        DateUtil.getInstance().setDateRaw(rawDate); // set to some date
        DateUtil.getInstance().setToCurrent(); // reset to current
        // check
        hasDefaultAllFieldsValid();
    }

    @Test
    public void doesFormatDateAsMonthLiteralDayOrdinalYear() {
        String dateString = DateUtil
                .getInstance()
                .setDateRaw(rawDate)
                .getDateAsMonthLiteralDayOrdinalYear();
        assertThat("error date as month literal", dateString, is("October 3rd, 2016"));
    }

    @Test
    public void doesFormatDateAsDayMonthDayOrdinal() {
        String dateString = DateUtil
                .getInstance()
                .setDateRaw(rawDate)
                .getDateAsDayMonthDayOrdinal();
        assertThat("error date as day month...", dateString, is("Monday, October 3rd"));

    }

    @Test
    public void doesFormatTime12Hour() {
        String dateString = DateUtil
                .getInstance()
                .setDateRaw(rawDate)
                .getTime12Hour();
        assertThat("error time 12-hour...", dateString, is("4:30 PM"));
    }

    @Test
    public void doesFormatDateAsMMddyyyy() {
        String dateString = DateUtil
                .getInstance()
                .setDateRaw(rawDate)
                .toStringWithFormatMmDashDdDashYyyy();
        assertThat("error format MM-dd-yyyy...", dateString, is("10-03-2016"));
    }

    @Test
    public void doesReturnRawDate() {
        DateUtil.getInstance().setDateRaw(rawDate); // set some date
        String dateString = DateUtil.getInstance().toStringWithFormatIso8601(); // generate raw
        assertThat("error convert back to raw", dateString, is(rawDate));
    }

    @Test
    public void doesCompare() {
        DateUtil.getInstance().setDateRaw(rawDate);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, Calendar.JANUARY); // generate a date before
        Date dateAhead = calendar.getTime();

        assertThat("error compare (before)", DateUtil.getInstance().compareTo(dateAhead), is(1));

        calendar.set(Calendar.DAY_OF_MONTH, 23);
        calendar.set(Calendar.MONTH, Calendar.OCTOBER);
        Date dateAfter = calendar.getTime();

        assertThat("error compare (after)", DateUtil.getInstance().compareTo(dateAfter), is(-1));

    }

    @Test
    public void isToday() {
        DateUtil.getInstance().setToCurrent();
        assertThat("error test today", DateUtil.getInstance().isToday(), is(true));
    }

    @Test
    public void isYesterdayOrBefore() {
        Calendar calendar = Calendar.getInstance();
        calendar.roll(Calendar.DAY_OF_MONTH, -1); // move the calendar to yesterday
        Date yesterday = calendar.getTime();

        DateUtil.getInstance().setDate(yesterday);
        assertThat("error test yesterday", DateUtil.getInstance().isYesterdayOrBefore(), is(true));
    }

    @Test
    public void isTomorrowOrLater() {
        Calendar calendar = Calendar.getInstance();
        calendar.roll(Calendar.DAY_OF_MONTH, 1); // move the calendar to tomorrow
        Date yesterday = calendar.getTime();

        DateUtil.getInstance().setDate(yesterday);
        assertThat("error test tommorrow", DateUtil.getInstance().isTomorrowOrAfter(), is(true));

    }


    @Test
    public void numberOfDays(){
        Date start = new Date(2012, 1, 1);
        Date end = new Date(2017, 1, 1);

        int days = DateUtil.getInstance().getDaysElapsed(start, end);
        Assert.assertEquals(1827, days);
    }

}