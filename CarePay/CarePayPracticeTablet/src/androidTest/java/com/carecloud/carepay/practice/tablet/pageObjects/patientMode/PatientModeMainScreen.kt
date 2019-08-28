package com.carecloud.carepay.practice.tablet.pageObjects.patientMode

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * @author pjohnson on 2019-08-28.
 */
class PatientModeMainScreen : CustomViewActions() {

    init {
        verifyViewVisible(appContext.getString(R.string.content_description_patient_mode_main))
    }

    fun pressCheckInButton(): HowToLoginScreen {
        click(appContext.getString(R.string.content_description_check_in))
        return HowToLoginScreen()
    }
}