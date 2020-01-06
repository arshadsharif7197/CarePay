package com.carecloud.carepay.practice.tablet.tests.patientMode.payments.paymentPlan

import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.PracticeMainScreen
import com.carecloud.carepay.practice.tablet.tests.BaseTest
import org.junit.Test

/**
 * @author pjohnson on 2019-09-13.
 */
class PaymentPlanTest :BaseTest(){

    @Test
    fun a_createPaymentPlanTest() {
        PracticeMainScreen()
                .pressChangeModeButton()
                .pressPatientModeButton()
                .pressLetsStartButton()
                .pressPaymentButton()
                .pressLoginButton()
                .typeUsername("dev_emails+dev.rsanchez@carecloud.com")
                .typePassword("Test123!")
                .pressLoginButton()
                .selectFirstPayment()
                .clickCreateButton()
                .typeAmount("150")
                .clickCreateButton()
                .typePlanName("Automated Plan")
                .typeNumberOfMonths("5")
                .clickCreateButton()
                .chooseCreditCardMethod(0)
                .chooseCreditCard()
                .acceptTermsAndConditions()
                .clickOkButton()
    }
}