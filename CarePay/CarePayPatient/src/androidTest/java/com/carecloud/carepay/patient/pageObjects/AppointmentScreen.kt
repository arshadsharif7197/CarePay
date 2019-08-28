package com.carecloud.carepay.patient.pageObjects

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * @author pjohnson on 2019-08-28.
 */
class AppointmentScreen : CustomViewActions() {
    init {
//        EspressoIdlingResource.increment()
//        Thread.sleep(5000)
//        EspressoIdlingResource.decrement()
        verifyViewVisible(appContext.getString(R.string.content_description_appointment_screen))
    }
}