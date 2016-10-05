package com.carecloud.carepaylibrary;

import com.carecloud.carepaylibray.utils.DateUtil;

import org.junit.Test;

import java.util.Calendar;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


/**
 * Created by lsoco_user on 10/5/2016.
 * Tests for the DateUtil
 */
public class DateUtilTest {
    final String[] weekDays = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    final String[] weekDaysAbbrs = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    final String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    final String[] monthsAbbr = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    final String[] amPm = {"AM", "PM"};

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
    public void hasSetAllFieldsValid() {
        // test set date
        String rawDate = "2016-10-03T18:16:30-04:00";
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
}
