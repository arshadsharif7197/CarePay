package com.carecloud.carepay.practice.tablet.pageObjects.patientMode.checkin

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.pageObjects.patientMode.PatientModeMainScreen
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-10-01.
 */
class CheckInConfirmation: CustomViewActions() {
    fun goHome(): PatientModeMainScreen {
        click(appContext.getString(R.string.content_description_go_home_button))
        return PatientModeMainScreen()
    }
}