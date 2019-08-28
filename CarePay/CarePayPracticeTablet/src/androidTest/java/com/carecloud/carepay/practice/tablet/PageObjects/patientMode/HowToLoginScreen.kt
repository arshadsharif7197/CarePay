package com.carecloud.carepay.practice.tablet.PageObjects.patientMode

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.Tests.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * @author pjohnson on 2019-08-28.
 */
class HowToLoginScreen : CustomViewActions() {

    init {
        verifyViewVisible(appContext.getString(R.string.content_description_how_to_login_screen))
    }

    fun pressLoginButton(): LoginScreen {
        click(appContext.getString(R.string.content_description_how_to_login_login_button))
        return LoginScreen()
    }

}