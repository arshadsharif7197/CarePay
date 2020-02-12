package com.carecloud.carepay.patient.pageObjects.appointments

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.test_module.actions.CustomViewActions
import com.carecloud.test_module.actions.stringHolder

/**
 * Created by drodriguez on 2019-08-28.
 */

var appointmentTime = ""
var providerName = ""

class AddAppointmentFlow<T>(private val screenAfterAppointment: T) : CustomViewActions() {
    fun selectLocation() : AddAppointmentFlow<T> {
        click(appContext.getString(R.string.content_description_choose_location))
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_location_list), 1)
        return this
    }
    fun selectProvider(providerName: String): AddAppointmentFlow<T> {
        click(appContext.getString(R.string.content_description_choose_provider))
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_provider_list_2), textMatch = providerName)
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

    fun switchBE(chosenBe: String) : AddAppointmentFlow<T> {
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_list_of_practices), chosenBe)
        return this
    }
}