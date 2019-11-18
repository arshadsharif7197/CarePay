package com.carecloud.carepay.practice.tablet.pageObjects.patientMode

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.pageObjects.shared.login.LoginScreen
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * @author pjohnson on 2019-08-28.
 */
class HowToLoginScreen<T>(private val screenAfterLogin: T) : CustomViewActions() {
    init {
        verifyViewVisible(appContext.getString(R.string.content_description_how_to_login_screen))
    }

    fun pressLoginButton(): LoginScreen<T> {
        click(appContext.getString(R.string.content_description_how_to_login_login_button))
        return LoginScreen(screenAfterLogin)
    }

}