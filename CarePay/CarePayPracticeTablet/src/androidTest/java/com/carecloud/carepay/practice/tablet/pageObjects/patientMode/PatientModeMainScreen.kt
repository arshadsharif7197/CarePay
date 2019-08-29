package com.carecloud.carepay.practice.tablet.pageObjects.patientMode

import com.carecloud.carepay.practice.tablet.PageObjects.PatientMode.AddAppointmentFlow
import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * @author pjohnson on 2019-08-28.
 */
class PatientModeMainScreen : CustomViewActions() {

    fun pressCheckInButton(): HowToLoginScreen<CheckInScreen> {
        click(appContext.getString(R.string.content_description_check_in))
        return HowToLoginScreen(screenAfterLogin = CheckInScreen())
    }
    fun pressAppointmentButton(): HowToLoginScreen<AddAppointmentFlow<PatientModeMainScreen>> {
        click(appContext.getString(R.string.content_description_appointments_button))
        return HowToLoginScreen(screenAfterLogin = AddAppointmentFlow(screenAfterAppointment = PatientModeMainScreen()))
    }
}