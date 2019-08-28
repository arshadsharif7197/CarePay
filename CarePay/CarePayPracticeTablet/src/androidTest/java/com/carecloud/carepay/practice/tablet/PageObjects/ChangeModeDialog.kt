package com.carecloud.carepay.practice.tablet.PageObjects

import com.carecloud.carepay.practice.tablet.PageObjects.patientMode.SplashScreen
import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.Tests.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * @author pjohnson on 2019-08-28.
 */
class ChangeModeDialog : CustomViewActions() {

    init {
        verifyViewVisible(appContext.getString(R.string.content_description_change_mode_dialog))
    }

    fun pressPatientModeButton(): SplashScreen {
        click(appContext.getString(R.string.content_description_patient_mode))
        return SplashScreen()
    }
}