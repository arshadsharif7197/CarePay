package com.carecloud.carepay.practice.tablet.PageObjects

import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions
import com.carecloud.carepay.practice.tablet.PageObjects.Appointments.AppointmentsScreen
import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.Tests.appContext

/**
 * Created by drodriguez on 08/12/19.
 */

class PracticeMainScreen : CustomViewActions() {
    init {
        verifyViewVisible(appContext.getString(R.string.content_description_appointments))
    }

    fun pressPaymentButton() {
        click(appContext.getString(R.string.content_description_payments))
    }

    fun pressAppointmentsButton(): AppointmentsScreen {
        click(appContext.getString(R.string.content_description_appointments))
        return AppointmentsScreen()
    }

}
