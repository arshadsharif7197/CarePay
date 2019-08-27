package com.carecloud.carepay.practice.tablet.PageObjects

import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions
import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.Tests.appContext

/**
 * Created by drodriguez on 08/12/19.
 */

class SelectLocationDialog : CustomViewActions() {
    init {
        verifyViewVisible(appContext.getString(R.string.content_description_continue))
    }

    fun selectLocation(locationName: String): SelectLocationDialog {
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_provider_list), 2)
        return this
    }

    fun pressContinue(): PracticeMainScreen {
        click(appContext.getString(R.string.content_description_continue))
        return PracticeMainScreen()
    }
}
