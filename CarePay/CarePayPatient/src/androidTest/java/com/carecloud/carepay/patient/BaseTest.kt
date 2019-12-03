package com.carecloud.carepay.patient

import android.content.Context
import androidx.test.espresso.IdlingRegistry
import androidx.test.platform.app.InstrumentationRegistry
import com.carecloud.carepay.patient.patientsplash.SplashActivity
import androidx.test.rule.ActivityTestRule
import com.carecloud.carepay.patient.pageObjects.LoginScreen
import com.carecloud.carepay.patient.pageObjects.TutorialScreen

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
    var activityRule = ActivityTestRule(SplashActivity::class.java)

    @Before
    open fun setup() {
        // used for checking if network calls are going on, will pause test until call is finished
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource())
        appContext = InstrumentationRegistry.getInstrumentation().targetContext

        TutorialScreen()
                .skipTutorial()
                .typeUser("dev_emails+qa.androidbreeze2@carecloud.com")
                .typePassword("Test123!")
                .pressLoginButton()
    }

    @After
    open fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource())
    }
}
