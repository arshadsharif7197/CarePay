package com.carecloud.carepay.practice.tablet.pageObjects.patientMode.checkin

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.test_module.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-10-01.
 */
class CheckInDemographics: CustomViewActions() {
    fun <T>demographicsNextStep(next: T): T {
        click(appContext.getString(R.string.content_description_next_button))
        return next
    }
}