package com.carecloud.carepaylibray.androidTest.graphqlrequests

import android.os.Build
import androidx.annotation.RequiresApi
import com.carecloud.carepaylibray.androidTest.graphqldatamodels.Response
import com.carecloud.carepaylibray.androidTest.providers.*
import java.time.LocalDateTime

/**
 * Created by drodriguez on 2019-10-15.
 */
@RequiresApi(Build.VERSION_CODES.O)
fun createAppointment(): Response {
    val startTime = LocalDateTime.now()
    val endTime = LocalDateTime.now().plusHours(1)
    return makeRequest(formatRequest("""
        mutation { createAppointment(
            patientId: 47335654
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

fun createSimpleCharge(amount: Int = 100): Response {
    return makeRequest(formatRequest("""
        mutation { createSimpleCharge(
            patientId: 47335654
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
