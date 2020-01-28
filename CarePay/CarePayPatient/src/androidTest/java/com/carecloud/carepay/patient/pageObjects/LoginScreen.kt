package com.carecloud.carepay.patient.pageObjects

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.carepay.patient.pageObjects.appointments.AppointmentScreen
import com.carecloud.test_module.actions.CustomViewActions

/**
 * @author pjohnson on 2019-08-28.
 */
class LoginScreen : CustomViewActions() {

    init {
        verifyViewVisible(appContext.getString(R.string.content_description_signin))
    }

    fun typeUser(user: String): LoginScreen {
        type(appContext.getString(R.string.content_description_signin_user), user)
        return this
    }

    fun typePassword(password: String): LoginScreen {
        type(appContext.getString(R.string.content_description_signin_password), password)
        return this
    }

    fun pressLoginButton(): AppointmentScreen {
        click(appContext.getString(R.string.content_description_signin_button))
        return AppointmentScreen()
    }
}