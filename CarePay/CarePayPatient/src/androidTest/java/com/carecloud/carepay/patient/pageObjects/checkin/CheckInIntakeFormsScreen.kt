package com.carecloud.carepay.patient.pageObjects.checkin

import com.carecloud.test_module.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-09-19.
 */
class CheckInIntakeFormsScreen: CustomViewActions() {
    fun scrollToBottomOfIntake(): CheckInIntakeFormsScreen {
        wait(milliseconds = 5000)
        scrollWebView()
        return this
    }

    fun goToNextStep(): CheckInIntakeFormsScreen {
        clickOnSpecificText("NEXT STEP")
        return this
    }
}