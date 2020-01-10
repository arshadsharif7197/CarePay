package com.carecloud.carepay.patient.pageObjects.checkin.demographics

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.test_module.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-09-19.
 */
class CheckInDemogAddressScreen: CustomViewActions() {
    fun <T> addressNextStep(nextScreen: T): T {
        clickOnSpecificText("NEXT STEP")
        return nextScreen
    }
}