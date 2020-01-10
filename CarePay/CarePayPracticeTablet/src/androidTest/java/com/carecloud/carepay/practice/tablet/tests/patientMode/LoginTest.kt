package com.carecloud.carepay.practice.tablet.tests.patientMode

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.PracticeMainScreen
import com.carecloud.carepay.practice.tablet.tests.BaseTest
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author pjohnson on 2019-08-28.
 */
@RunWith(AndroidJUnit4::class)
class LoginTest : BaseTest(){

    @Test
    fun patientModeLoginTest() {
        PracticeMainScreen()
                .pressChangeModeButton()
                .pressPatientModeButton()
                .pressLetsStartButton()
                .pressCheckInButton()
                .pressLoginButton()
                .typeUsername("dev_emails+qa.androidbreeze2@carecloud.com")
                .typePassword("Test123!")
                .pressLoginButton()
    }
}