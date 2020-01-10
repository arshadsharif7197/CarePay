package com.carecloud.carepay.practice.tablet.pageObjects.patientMode.checkin

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.pageObjects.patientMode.PatientModeMainScreen
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.test_module.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-10-01.
 */
class CheckInOutConfirmation: CustomViewActions() {
    fun goHome(): PatientModeMainScreen {
        click(appContext.getString(R.string.content_description_go_home_button), true)
        return PatientModeMainScreen()
    }

    fun verifyAppointmentStatus(status: String): CheckInOutConfirmation {
        verifyTextOnView(appContext.getString(R.string.content_description_appointment_status_text_view), status)
        return this
    }
}