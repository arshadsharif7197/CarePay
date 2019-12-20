package com.carecloud.carepay.practice.tablet.pageObjects.patientMode.checkin

import com.carecloud.test_module.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-10-01.
 */
class CheckInAllergies: CustomViewActions() {
    fun allergiesNextStep():CheckInOutConfirmation {
        clickOnSpecificText("NEXT STEP")
        return CheckInOutConfirmation()
    }
}