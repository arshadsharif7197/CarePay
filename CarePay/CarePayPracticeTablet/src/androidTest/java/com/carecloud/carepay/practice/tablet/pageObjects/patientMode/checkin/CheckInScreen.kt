package com.carecloud.carepay.practice.tablet.pageObjects.patientMode.checkin

import com.carecloud.test_module.actions.CustomViewActions

/**
 * @author pjohnson on 2019-08-28.
 */
class CheckInScreen : CustomViewActions() {
    fun checkInAppointment(): CheckInPersonalInfo {
        clickOnSpecificText("START CHECK-IN")
        return CheckInPersonalInfo()
    }
}