package com.carecloud.carepay.practice.tablet.pageObjects.patientMode.checkin

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-09-30.
 */
class CheckInAddress: CustomViewActions() {
    fun addressNextStep(): CheckInDemographics {
        click(appContext.getString(R.string.content_description_next_button))
        return CheckInDemographics()
    }
}