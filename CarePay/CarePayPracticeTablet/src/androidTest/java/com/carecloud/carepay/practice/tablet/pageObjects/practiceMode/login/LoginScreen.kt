package com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.login

import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions
import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.tests.appContext

/**
 * Created by drodriguez on 08/12/19.
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

    fun pressLoginButton(): SelectPracticeDialog {
        click(appContext.getString(R.string.content_description_sign_in))
        return SelectPracticeDialog()
    }
}
