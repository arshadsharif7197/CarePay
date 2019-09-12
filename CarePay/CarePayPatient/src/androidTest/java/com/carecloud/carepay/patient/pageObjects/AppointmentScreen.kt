package com.carecloud.carepay.patient.pageObjects

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.carepay.patient.pageObjects.appointments.AddAppointmentFlow
import com.carecloud.carepay.patient.pageObjects.payments.PaymentScreen
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

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

    fun openPaymentScreen(): PaymentScreen {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
        clickOnSpecificId(R.id.paymentsMenuItem)
        return PaymentScreen()
    }
}