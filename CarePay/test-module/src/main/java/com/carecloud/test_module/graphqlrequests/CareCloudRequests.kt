package com.carecloud.carepaylibray.androidTest.graphqlrequests

import android.os.Build
import androidx.annotation.RequiresApi
import com.carecloud.carepaylibray.androidTest.graphqldatamodels.Response
import com.carecloud.carepaylibray.androidTest.providers.formatRequest
import com.carecloud.carepaylibray.androidTest.providers.makeRequest
import com.carecloud.carepaylibray.androidTest.providers.xavierToken
import java.time.LocalDateTime
import java.time.ZoneId


/**
 * Created by drodriguez on 2019-10-15.
 */
@RequiresApi(Build.VERSION_CODES.O)
fun createAppointment(patientId: Int = 47335654): Response {
    val zid = ZoneId.of("US/Eastern")
    val startTime = LocalDateTime.now(zid)
    val endTime = LocalDateTime.now(zid).plusHours(1)
    return makeRequest(formatRequest("""
        mutation { createAppointment(
            patientId: $patientId
            startTime: "$startTime"
            endTime: "$endTime"
            locationId: 23667
            providerId: 19942
            appointmentStatus: "pending"
            businessEntityId: 4294
            natureOfVisitId: 61857
            resourceId: 19313)
            {
            id
            start_time
            }
        }
    """), xavierToken)
}

fun checkinAppointment(id: Int?): Response {
    return makeRequest(formatRequest("""
        mutation { checkInAppointment(appointmentId: $id, businessEntityId: 4294)
        }
    """, false), xavierToken)
}

fun createSimpleCharge(amount: Int = 100, patientId: Int = 47335654): Response {
    return makeRequest(formatRequest("""
        mutation { createSimpleCharge(
            patientId: $patientId
            locationId: 23667
            providerId: 19942
    	    businessEntityId: 4294
            simpleChargeType: 64232
            simpleChargeAmount: $amount)
            {
            id
            }
        }
    """, false), xavierToken)
}

fun deleteAppointment(id: Int?): Response {
    return makeRequest(formatRequest("""
        mutation { deleteAppointment(appointmentId: $id, businessEntityId: 4294)
        }
    """, false), xavierToken)
}
