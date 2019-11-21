package com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.appointments

import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions
import com.carecloud.carepay.practice.tablet.pageObjects.shared.appointments.AddAppointmentFlow
import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.pageObjects.patientMode.checkin.CheckInPersonalInfo
import com.carecloud.carepay.practice.tablet.pageObjects.patientMode.checkout.CheckOutNextAppointmentScreen
import com.carecloud.carepay.practice.tablet.tests.appContext

/**
 * Created by drodriguez on 08/12/19.
 */

class AppointmentsScreen : CustomViewActions() {
    fun pressAddAppointmentButton(): AppointmentsScreen {
        click(appContext.getString(R.string.content_description_add_appointment))
        return this
    }

    fun searchForPatient(patient: String): AddAppointmentFlow<AppointmentsScreen> {
        type(appContext.getString(R.string.content_description_patient_search), patient + "\n")
        wait(milliseconds = 2000)
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_patient_list), 0)
        return AddAppointmentFlow(screenAfterAppointment = AppointmentsScreen())
    }

    fun verifyAppointmentIsOnList(textMatch: String) : AppointmentsScreen {
        wait(milliseconds = 1000)
        verifyItemInRecyclerView(appContext.getString(R.string.content_description_appointments_list), textMatch, false)
        return this
    }

    fun checkInFirstAppointmentOnList(): CheckInPersonalInfo {
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_appointments_list), 0)
        clickOnSpecificText("Check-in")
        type(appContext.getString(R.string.content_description_email), "01011990", true)
        click(appContext.getString(R.string.content_description_sign_in))
        return CheckInPersonalInfo()
    }

    fun checkInAppointmentAtTime(appointmentTime: String): CheckInPersonalInfo {
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_appointments_list), appointmentTime)
        clickOnSpecificText("Check-in")
        type(appContext.getString(R.string.content_description_email), "01011990", true)
        click(appContext.getString(R.string.content_description_sign_in))
        return CheckInPersonalInfo()
    }

    fun checkOutFirstAppointmentOnList(): CheckOutNextAppointmentScreen {
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_appointments_list), 0)
        clickOnSpecificText("Check-out")
        return CheckOutNextAppointmentScreen()
    }
}
