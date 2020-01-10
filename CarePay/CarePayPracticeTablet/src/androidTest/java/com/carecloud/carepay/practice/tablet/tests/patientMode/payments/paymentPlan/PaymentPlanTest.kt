package com.carecloud.carepay.practice.tablet.tests.patientMode.payments.paymentPlan

import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.PracticeMainScreen
import com.carecloud.carepay.practice.tablet.tests.BaseTest
import com.carecloud.test_module.data.PatientData
import com.carecloud.test_module.graphqlrequests.createSimpleCharge
import com.carecloud.test_module.providers.initXavierProvider
import org.junit.Before
import org.junit.Test

/**
 * @author pjohnson on 2019-09-13.
 */
class PaymentPlanTest :BaseTest(){

    private val patient = PatientData.patient8
    private val amount = 100

    @Before
    override
    fun setup() {
        initXavierProvider()
        createSimpleCharge(amount, patient.id)
        super.setup()
    }

    @Test
    fun a_createPaymentPlanTest() {
        PracticeMainScreen()
                .pressChangeModeButton()
                .pressPatientModeButton()
                .pressLetsStartButton()
                .pressPaymentButton()
                .pressLoginButton()
                .typeUsername(patient.email)
                .typePassword("Test123!")
                .pressLoginButton()
                .selectFirstPayment()
                .clickCreateButton()
                .typeAmount(amount.toString())
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