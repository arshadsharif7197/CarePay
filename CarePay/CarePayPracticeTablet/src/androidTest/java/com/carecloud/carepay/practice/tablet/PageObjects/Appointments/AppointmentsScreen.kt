package com.carecloud.carepay.practice.tablet.PageObjects.Appointments

import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions
import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.Tests.appContext

/**
 * Created by drodriguez on 08/12/19.
 */

class AppointmentsScreen : CustomViewActions() {
    init {
        verifyViewVisible(appContext.getString(R.string.content_description_add_appointment))
    }

    fun pressAddAppointmentButton(): AppointmentsScreen {
        click(appContext.getString(R.string.content_description_add_appointment))
        return this
    }

    fun searchForPatient(): AddAppointmentDialog {
        type(appContext.getString(R.string.content_description_patient_search), "qa\n")
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_patient_list), 0)
        return AddAppointmentDialog()
    }
}
