package com.carecloud.carepay.patient.pageObjects

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.test_module.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-12-02.
 */
class TutorialScreen: CustomViewActions() {
    fun skipTutorial(): LoginScreen {
        click(appContext.getString(R.string.content_description_skip_tutorial))
        return LoginScreen()
    }
}