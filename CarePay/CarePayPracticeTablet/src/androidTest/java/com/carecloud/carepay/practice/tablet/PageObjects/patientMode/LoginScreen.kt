package com.carecloud.carepay.practice.tablet.PageObjects.patientMode

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.Tests.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * @author pjohnson on 2019-08-28.
 */
class LoginScreen : CustomViewActions() {

    init {
        verifyViewVisible(appContext.getString(R.string.content_description_email))
    }

    fun typeUsername(username: String): LoginScreen {
        type(appContext.getString(R.string.content_description_email), username, true)
        return this
    }

    fun typePassword(password: String): LoginScreen {
        type(appContext.getString(R.string.content_description_password), password, true)
        return this
    }

    fun pressLoginButton(): CheckInScreen {
        click(appContext.getString(R.string.content_description_sign_in))
        return CheckInScreen()
    }

}