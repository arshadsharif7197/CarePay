package com.carecloud.carepay.practice.tablet.pageObjects.patientMode

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * @author pjohnson on 2019-08-28.
 */
class CheckInScreen : CustomViewActions() {

    init{
        verifyViewVisible(appContext.getString(R.string.content_description_check_in_screen))
    }
}