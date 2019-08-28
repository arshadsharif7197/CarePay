package com.carecloud.carepay.practice.tablet.Tests.patientMode

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.practice.tablet.PageObjects.PracticeMainScreen
import com.carecloud.carepay.practice.tablet.Tests.BaseTest
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author pjohnson on 2019-08-28.
 */
@RunWith(AndroidJUnit4::class)
class LoginTest :BaseTest(){

    @Test
    fun patientModeLoginTest() {
        PracticeMainScreen()
                .pressChangeModeButton()
                .pressPatientModeButton()
                .pressLetsStartButton()
                .pressCheckInButton()
                .pressLoginButton()
                .typeUsername("dev_emails+dev.rsanchez@carecloud.com")
                .typePassword("Test123!")
                .pressLoginButton()
    }
}