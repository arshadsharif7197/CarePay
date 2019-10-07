package com.carecloud.carepay.practice.tablet.pageObjects

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * @author pjohnson on 2019-09-10.
 */
class ConfirmationScreen : CustomViewActions() {

    fun confirm() {
        click(appContext.getString(R.string.content_description_confirm))
    }

}