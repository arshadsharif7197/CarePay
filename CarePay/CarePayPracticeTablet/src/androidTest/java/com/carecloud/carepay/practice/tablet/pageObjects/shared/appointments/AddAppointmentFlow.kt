package com.carecloud.carepay.practice.tablet.pageObjects.shared.appointments

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.appointments.AppointmentsScreen
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.test_module.actions.CustomViewActions
import com.carecloud.test_module.actions.stringHolder

/**
 * Created by drodriguez on 2019-08-28.
 */

var appointmentTime = ""

class AddAppointmentFlow<T>(private val screenAfterAppointment: T) : CustomViewActions() {
    fun selectLocation() : AddAppointmentFlow<T> {
        click(appContext.getString(R.string.content_description_choose_location), screenAfterAppointment is AppointmentsScreen)
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_location_list), 0)
        return this
    }
    fun selectProvider(): AddAppointmentFlow<T> {
        click(appContext.getString(R.string.content_description_choose_provider))
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_provider_list_2), 0)
        return this
    }
    fun selectVisitType(): AddAppointmentFlow<T> {
        click(appContext.getString(R.string.content_description_choose_visit_type))
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_visit_type_list), 0)
        return this
    }

    fun pressCheckAvailableTimes(): AddAppointmentFlow<T> {
        click(appContext.getString(R.string.content_description_check_available_times_button))
        return this
    }

    fun chooseAppointmentTime(): AddAppointmentFlow<T> {
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_available_hours_list), 1)
        appointmentTime = stringHolder[0]
        return this
    }

    fun pressScheduleAppointment(): T {
        click(appContext.getString(R.string.content_description_request_appointment))
        return screenAfterAppointment
    }
}