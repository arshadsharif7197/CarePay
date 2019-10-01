package com.carecloud.carepay.patient.pageObjects.checkin.demographics

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.carepay.patient.pageObjects.checkin.CheckInMedicationsScreen
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-09-19.
 */
class CheckInDemogDemographicsScreen: CustomViewActions() {
    fun demographicsNextStep(): CheckInMedicationsScreen {
        click(appContext.getString(R.string.content_description_next_button))
        return CheckInMedicationsScreen()
    }
}