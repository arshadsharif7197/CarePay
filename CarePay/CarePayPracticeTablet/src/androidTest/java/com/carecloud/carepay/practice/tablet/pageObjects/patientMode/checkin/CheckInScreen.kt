package com.carecloud.carepay.practice.tablet.pageObjects.patientMode.checkin

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * @author pjohnson on 2019-08-28.
 */
class CheckInScreen : CustomViewActions() {
    fun <T> checkInAppointment(next: T, appointmentTime: String): T {
        click("START CHECK-IN AT $appointmentTime")
        return next
    }
}