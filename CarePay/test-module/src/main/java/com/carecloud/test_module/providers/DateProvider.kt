package com.carecloud.carepaylibray.androidTest.providers

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Created by drodriguez on 2019-10-29.
 */

@RequiresApi(Build.VERSION_CODES.O)
fun formatAppointmentTime(startTime: String, addLeadingZero: Boolean = false): String {
    val appointmentTimeFormat = DateTimeFormatter.ofPattern(if (addLeadingZero) "hh:mm a" else "h:mm a")
    val date = ZonedDateTime.parse(startTime).withZoneSameInstant(ZoneId.systemDefault())
    return date.format(appointmentTimeFormat)
}

//fun verifyTimeAgainstTimeZone