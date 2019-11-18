package com.carecloud.carepaylibray.androidTest.graphql

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime

/**
 * Created by drodriguez on 2019-10-15.
 */

@RequiresApi(Build.VERSION_CODES.O)
fun createAppointment(): String {
    val startTime = LocalDateTime.now()
    val endTime = LocalDateTime.now().plusHours(1)
    return createRequest("""
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
            }
        }
    """)
}

fun checkinAppointment(id: Int?): String {
    return createRequest("""
        mutation { checkInAppointment(appointmentId: $id, businessEntityId: 4294)
        }
    """, false)
}

fun createSimpleCharge(): String {
    return createRequest("""
        mutation { createSimpleCharge(
            patientId: 47335654
            locationId: 23667
            providerId: 19942
    	    businessEntityId: 4294
            simpleChargeType: 64232
            simpleChargeAmount: 100)
            {
            id
            }
        }
    """, false)
}

fun deleteAppointment(id: Int?): String {
    return createRequest("""
        mutation { deleteAppointment(appointmentId: $id, businessEntityId: 4294)
        }
    """, false)
}

