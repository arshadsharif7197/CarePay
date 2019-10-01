package com.carecloud.carepay.practice.tablet.pageObjects.patientMode.checkin

import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-09-30.
 */
class CheckInPersonalInfo: CustomViewActions() {
    fun personalInfoNextStep(): CheckInAddress {
        clickOnSpecificText("NEXT STEP")
        return CheckInAddress()
    }
}