package com.carecloud.carepay.practice.tablet.tests.patientMode.payments

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.PracticeMainScreen
import com.carecloud.carepay.practice.tablet.tests.BaseTest
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by drodriguez on 2019-09-11.
 */
@RunWith(AndroidJUnit4::class)
class PMPartialMakePayment: BaseTest() {

    @Test
    fun pmPartialMakePayment() {
        PracticeMainScreen()
                .pressChangeModeButton()
                .pressPatientModeButton()
                .pressLetsStartButton()
                .pressPaymentButton()
                .pressLoginButton()
                .typeUsername("dev_emails+qa.androidbreeze2@carecloud.com")
                .typePassword("Test123!")
                .pressLoginButton()
                .selectFirstPayment()
                .makePartialPayment()
                .enterAmount("20")
                .payUseCreditCardOnFile(null)
    }
}