package com.carecloud.carepay.practice.tablet.pageObjects.practiceMode

import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions
import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.appointments.AppointmentsScreen
import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.pageObjects.ChangeModeDialog
import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.payments.PaymentsScreen
import com.carecloud.carepay.practice.tablet.tests.appContext

/**
 * Created by drodriguez on 08/12/19.
 */

class PracticeMainScreen : CustomViewActions() {
    init {
        verifyViewVisible(appContext.getString(R.string.content_description_appointments))
    }

    fun pressPaymentButton(): PaymentsScreen {
        click(appContext.getString(R.string.content_description_payments))
        return PaymentsScreen()
    }

    fun pressAppointmentsButton(): AppointmentsScreen {
        click(appContext.getString(R.string.content_description_appointments))
        return AppointmentsScreen()
    }

    fun pressChangeModeButton(): ChangeModeDialog {
        click(appContext.getString(R.string.content_description_change_mode))
        return ChangeModeDialog()
    }

}
