package com.carecloud.carepay.patient.pageObjects.checkin.demographics

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-09-19.
 */
class CheckInDemogPersonalInfoScreen: CustomViewActions() {
    fun <T> personalInfoNextStep(next: T): T {
        clickOnSpecificText("NEXT STEP")
        return next
    }

}