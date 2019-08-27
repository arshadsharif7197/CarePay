package com.carecloud.carepay.practice.tablet.PageObjects.Appointments

import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions
import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.Tests.appContext

/**
 * Created by drodriguez on 08/12/19.
 */

class AddAppointmentDialog : CustomViewActions() {
    init {
        verifyViewVisible(appContext.getString(R.string.content_description_check_available_times))
    }

    fun chooseProvider(): AddAppointmentDialog {
        click(appContext.getString(R.string.content_description_choose_provider))
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_provider_list_2), 0)
        return this
    }

    fun chooseVisitType(): AddAppointmentDialog {
        click(appContext.getString(R.string.content_description_choose_visit_type))
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_visit_type_list), 0)
        return this
    }

    fun chooseLocation(): AddAppointmentDialog {
        click(appContext.getString(R.string.content_description_choose_location),true)
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_location_list), 0)
        return this
    }

    fun pressCheckAvailableTimes(): AddAppointmentDialog {
        click(appContext.getString(R.string.content_description_check_available_times))
        return this
    }

    fun chooseAppointmentTime(): AddAppointmentDialog {
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_available_hours_list), 1)
        return this
    }

    fun pressScheduleAppointment() {
        click(appContext.getString(R.string.content_description_request_appointment))
    }
}
