package com.carecloud.carepay.practice.tablet.pageObjects.patientMode.checkin

import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-10-01.
 */
class CheckInAllergies: CustomViewActions() {
    fun allergiesNextStep():CheckInConfirmation {
        clickOnSpecificText("NEXT STEP")
        return CheckInConfirmation()
    }
}