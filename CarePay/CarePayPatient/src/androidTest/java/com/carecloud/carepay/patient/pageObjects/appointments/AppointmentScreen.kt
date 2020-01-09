package com.carecloud.carepay.patient.pageObjects.appointments

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.carepay.patient.pageObjects.NavigationMenu
import com.carecloud.carepay.patient.pageObjects.checkin.demographics.CheckInDemogPersonalInfoScreen
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * @author pjohnson on 2019-08-28.
 */
class AppointmentScreen : CustomViewActions() {

    fun addNewAppointment() : AddAppointmentFlow<AppointmentScreen> {
        click(appContext.getString(R.string.content_description_add_appointment_button))
        return AddAppointmentFlow(screenAfterAppointment = AppointmentScreen())
    }

    fun verifyAppointmentIsOnList(textMatch: String) : AppointmentScreen {
        verifyItemInRecyclerView(appContext.getString(R.string.content_description_appointments_list), textMatch)
        return this
    }

    fun openNavigationDrawer(): NavigationMenu {
        click(appContext.getString(R.string.navigation_drawer_open))
        return NavigationMenu()
    }

    fun checkInFirstAppointmentOnList(position: Int): CheckInDemogPersonalInfoScreen {
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_appointments_list), position)
        click(appContext.getString(R.string.content_description_checkin_appointment_button))
        return CheckInDemogPersonalInfoScreen()
    }

    fun discardReviewPopup(): AppointmentScreen {
        click(appContext.getString(R.string.content_description_not_now_button))
        return this
    }

    fun checkOutFirstAppointmentOnList(position: Int): CheckOutNextAppointmentScreen {
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_appointments_list), position)
        click(appContext.getString(R.string.content_description_checkout_appointment_button))
        return CheckOutNextAppointmentScreen()
    }
}