package com.carecloud.carepay.practice.tablet.pageObjects.patientMode.checkout

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.pageObjects.patientMode.checkin.CheckInOutConfirmation
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.test_module.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-10-02.
 */
class CheckOutNextAppointmentScreen: CustomViewActions() {
    fun scheduleNewAppointment(): CheckInOutConfirmation {
        click(appContext.getString(R.string.content_description_visit_type_edit_text))
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_visit_type_list), "Abdominal Pain")
        click(appContext.getString(R.string.content_description_visit_time_edit_text))
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_available_hours_list), 1)
        click(appContext.getString(R.string.content_description_schedule_appointment_button))
        return CheckInOutConfirmation()
    }

    fun scheduleLater(): CheckInOutConfirmation {
        click(appContext.getString(R.string.content_description_schedule_later_button))
        return CheckInOutConfirmation()
    }
}