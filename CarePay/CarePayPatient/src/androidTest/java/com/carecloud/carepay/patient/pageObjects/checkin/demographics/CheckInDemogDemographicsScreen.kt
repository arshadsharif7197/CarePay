package com.carecloud.carepay.patient.pageObjects.checkin.demographics

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.carepay.patient.pageObjects.checkin.CheckInMedicationsScreen
import com.carecloud.test_module.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-09-19.
 */
class CheckInDemogDemographicsScreen: CustomViewActions() {
    fun <T> demographicsNextStep(next: T): T {
        clickOnSpecificText("NEXT STEP")
        return next
    }
}