package com.carecloud.carepay.patient.pageObjects.checkin

import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-09-19.
 */
class CheckInIntakeFormsScreen: CustomViewActions() {
    fun scrollToBottomOfIntake(): CheckInIntakeFormsScreen {
        wait(milliseconds = 5000)
        scrollWebView()
        return this
    }

    fun <T> goToNextStep(next: T): T {
        clickOnSpecificText("NEXT STEP")
        return next
    }
}