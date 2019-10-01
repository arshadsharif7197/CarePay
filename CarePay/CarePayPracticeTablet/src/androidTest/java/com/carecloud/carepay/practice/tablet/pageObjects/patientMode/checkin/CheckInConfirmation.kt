package com.carecloud.carepay.practice.tablet.pageObjects.patientMode.checkin

import com.carecloud.carepay.practice.tablet.pageObjects.patientMode.PatientModeMainScreen
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-10-01.
 */
class CheckInConfirmation: CustomViewActions() {
    fun goHome(): PatientModeMainScreen {
        clickOnSpecificText("Go Home")
        return PatientModeMainScreen()
    }
}