package com.carecloud.carepay.practice.tablet.pageObjects.patientMode

import com.carecloud.carepay.practice.tablet.pageObjects.shared.appointments.AddAppointmentFlow
import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.pageObjects.patientMode.checkin.CheckInScreen
import com.carecloud.carepay.practice.tablet.pageObjects.patientMode.checkout.CheckOutNextAppointmentScreen
import com.carecloud.carepay.practice.tablet.pageObjects.patientMode.payments.PaymentsScreen
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
    fun pressPaymentButton(): HowToLoginScreen<PaymentsScreen>{
        click(appContext.getString(R.string.content_description_payments_button))
        return HowToLoginScreen(screenAfterLogin = PaymentsScreen())
    }
    fun pressCheckOutButton(): HowToLoginScreen<CheckOutNextAppointmentScreen> {
        click(appContext.getString(R.string.content_description_check_out))
        return HowToLoginScreen(screenAfterLogin = CheckOutNextAppointmentScreen())
    }
}