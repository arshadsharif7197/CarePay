package com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.appointments

import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions
import com.carecloud.carepay.practice.tablet.pageObjects.shared.appointments.AddAppointmentFlow
import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.tests.appContext

/**
 * Created by drodriguez on 08/12/19.
 */

class AppointmentsScreen : CustomViewActions() {
    fun pressAddAppointmentButton(): AppointmentsScreen {
        click(appContext.getString(R.string.content_description_add_appointment))
        return this
    }

    fun searchForPatient(): AddAppointmentFlow<AppointmentsScreen> {
        type(appContext.getString(R.string.content_description_patient_search), "qa\n")
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_patient_list), 0)
        return AddAppointmentFlow(screenAfterAppointment = AppointmentsScreen())
    }

    fun verifyAppointmentIsOnList(textMatch: String) : AppointmentsScreen {
        wait(milliseconds = 1000)
        verifyItemInRecyclerView(appContext.getString(R.string.content_description_appointments_list), textMatch, false)
        return this
    }
}
