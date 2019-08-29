package com.carecloud.carepay.patient.pageObjects

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * @author pjohnson on 2019-08-28.
 */
class ChooseProfileScreen : CustomViewActions() {

    init {
        verifyViewNotVisible(appContext.getString(R.string.content_description_choose_profile_screen))
    }

    fun goToAppointments(): AppointmentScreen {
        return AppointmentScreen()
    }
}