package com.carecloud.carepay.practice.tablet.pageObjects.patientMode.checkin

import com.carecloud.test_module.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-10-01.
 */
class CheckInMedications: CustomViewActions() {
    fun <T> medicationsNextStep(next: T): T {
        clickOnSpecificText("NEXT STEP")
        return next
    }
}