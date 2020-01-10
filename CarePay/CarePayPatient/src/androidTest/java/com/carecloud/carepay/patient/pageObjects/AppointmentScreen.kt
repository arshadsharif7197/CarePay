package com.carecloud.carepay.patient.pageObjects

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.carepay.patient.pageObjects.appointments.AddAppointmentFlow
import com.carecloud.test_module.actions.CustomViewActions

/**
 * @author pjohnson on 2019-08-28.
 */
class AppointmentScreen : CustomViewActions() {

    fun addNewAppointment(): AddAppointmentFlow<AppointmentScreen> {
        click(appContext.getString(R.string.content_description_add_appointment_button))
        return AddAppointmentFlow(screenAfterAppointment = AppointmentScreen())
    }

    fun verifyAppointmentIsOnList(textMatch: String): AppointmentScreen {
        verifyItemInRecyclerView(appContext.getString(R.string.content_description_appointments_list), textMatch)
        return this
    }

    fun openNavigationDrawer(): NavigationMenu {
        click(appContext.getString(R.string.navigation_drawer_open))
        return NavigationMenu()
    }
}