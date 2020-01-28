package com.carecloud.carepay.patient.pageObjects.appointments

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.test_module.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-10-02.
 */
class CheckOutNextAppointmentScreen: CustomViewActions() {
    fun scheduleNewAppointment() {
        click(appContext.getString(R.string.content_description_visit_type_edit_text))
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_visit_type_list), "Abdominal Pain")
        click(appContext.getString(R.string.content_description_visit_time_edit_text))
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_available_hours_list), 1)
        click(appContext.getString(R.string.content_description_schedule_appointment_button))

    }

    fun scheduleAppointmentLater(): AppointmentScreen {
        click(appContext.getString(R.string.content_description_schedule_later_button))
        click(appContext.getString(R.string.content_description_ok_button))
        return AppointmentScreen()
    }
}