package com.carecloud.carepay.practice.tablet.PageObjects.patientMode

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.Tests.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * @author pjohnson on 2019-08-28.
 */
class SplashScreen : CustomViewActions() {
    init {
        verifyViewVisible(appContext.getString(R.string.content_description_patient_mode_splash))
    }

    fun pressLetsStartButton(): PatientModeMainScreen {
        click(appContext.getString(R.string.content_description_patient_mode_splash_continue), true)
        return PatientModeMainScreen()
    }
}