package com.carecloud.carepay.practice.tablet.tests

import android.content.Context
import androidx.test.espresso.IdlingRegistry
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.carecloud.carepay.practice.tablet.pageObjects.shared.login.LoginScreen

import com.carecloud.carepay.practice.tablet.PracticeTabletSplashActivity
import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.login.SelectPracticeDialog
import com.carecloud.carepay.service.library.EspressoIdlingResource

import org.junit.After
import org.junit.Before
import org.junit.Rule

/**
 * Created by drodriguez on 08/12/19.
 */

// Used to access the context of the app, global
lateinit var appContext: Context

open class BaseTest {

    // Specify the starting activity of the app
    @get:Rule
    var activityRule = ActivityTestRule(PracticeTabletSplashActivity::class.java)

    @Before
    fun setup() {
        // used for checking if network calls are going on, will pause test until call is finished
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource())
        appContext = InstrumentationRegistry.getInstrumentation().targetContext

        // Each test includes the same login steps
        LoginScreen(screenAfterLogin = SelectPracticeDialog())
                .typeUsername("rainforestbrzmanqa01@e.rainforest.com")
                .typePassword("Rainforest123#")
                .pressLoginButton()
                .selectPractice("CareCloud Automation Environment")
                .pressContinue()
                .selectLocation("EAST CITY CLINIC")
                .pressContinue()
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource())
    }
}
