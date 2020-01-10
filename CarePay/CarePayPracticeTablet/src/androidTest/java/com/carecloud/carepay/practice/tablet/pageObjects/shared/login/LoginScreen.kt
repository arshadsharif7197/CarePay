package com.carecloud.carepay.practice.tablet.pageObjects.shared.login

import com.carecloud.test_module.actions.CustomViewActions
import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.tests.appContext

/**
 * Created by drodriguez on 08/12/19.
 */

class LoginScreen<T>(private val screenAfterLogin: T) : CustomViewActions() {
    init {
        verifyViewVisible(appContext.getString(R.string.content_description_email))
    }

    fun typeUsername(username: String): LoginScreen<T> {
        type(appContext.getString(R.string.content_description_email), username, true)
        return this
    }

    fun typePassword(password: String): LoginScreen<T> {
        type(appContext.getString(R.string.content_description_password), password, true)
        return this
    }

    fun pressLoginButton(): T {
        click(appContext.getString(R.string.content_description_sign_in))
        return screenAfterLogin
    }
}
