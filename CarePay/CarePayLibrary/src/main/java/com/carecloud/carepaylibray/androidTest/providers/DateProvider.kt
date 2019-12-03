package com.carecloud.carepaylibray.androidTest.providers

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Created by drodriguez on 2019-10-29.
 */

@RequiresApi(Build.VERSION_CODES.O)
fun formatAppointmentTime(startTime: String): String {
    val appointmentTimeFormat = DateTimeFormatter.ofPattern("h:mm a")
    var date = ZonedDateTime.parse(startTime)
    return date.format(appointmentTimeFormat)
}