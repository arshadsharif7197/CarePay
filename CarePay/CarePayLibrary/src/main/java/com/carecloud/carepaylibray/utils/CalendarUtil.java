package com.carecloud.carepaylibray.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

/**
 * @author pjohnson on 4/22/19.
 */
public abstract class CalendarUtil {

    private CalendarUtil() {
    }

    public static Intent createSaveEventIntent(long eventId, String title, String description,
                                               long beginTime, long endtime, String location) {
        return new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra("_id", eventId)
                .putExtra(CalendarContract.Events.TITLE, "Yoga")
                .putExtra(CalendarContract.Events.DESCRIPTION, "Group class")
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endtime)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
    }

    public static long getNewEventId(Context context) {
        Uri local_uri = Uri.parse(getCalendarUriBase(context) + "events");
        Cursor cursor = context.getContentResolver().query(local_uri, new String[]{"MAX(_id) as max_id"},
                null, null, "_id");
        cursor.moveToFirst();
        long max_val = cursor.getLong(cursor.getColumnIndex("max_id"));
        return max_val + 1;
    }

    public static String getCalendarUriBase(Context context) {
        String calendarUriBase = null;
        Uri calendars = Uri.parse("content://calendar/calendars");
        Cursor managedCursor = null;

        try {
            managedCursor = context.getContentResolver().query(calendars,
                    null, null, null, null);
        } catch (Exception e) {
        }

        if (managedCursor != null) {
            calendarUriBase = "content://calendar/";
        } else {
            calendars = Uri.parse("content://com.android.calendar/calendar");
            try {
                managedCursor = context.getContentResolver().query(calendars,
                        null, null, null, null);
            } catch (Exception e) {
                calendarUriBase = "content://com.android.calendar/";
            }
            if (managedCursor != null) {
                calendarUriBase = "content://com.android.calendar/";
            }
        }

        return calendarUriBase;
    }
}
