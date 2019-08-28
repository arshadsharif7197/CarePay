package com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.login

import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions
import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.tests.appContext

/**
 * Created by drodriguez on 08/12/19.
 */

class SelectPracticeDialog : CustomViewActions() {
    init {
        verifyViewVisible(appContext.getString(R.string.content_description_continue))
    }

    fun selectPractice(practiceName: String): SelectPracticeDialog {
        clickOnSpecificText(practiceName)
        return this
    }

    fun pressContinue(): SelectLocationDialog {
        click(appContext.getString(R.string.content_description_continue))
        return SelectLocationDialog()
    }
}
