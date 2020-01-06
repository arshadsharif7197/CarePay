package com.carecloud.carepay.practice.tablet.tests.patientMode.payments

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.PracticeMainScreen
import com.carecloud.carepay.practice.tablet.tests.BaseTest
import com.carecloud.test_module.data.PatientData
import com.carecloud.test_module.graphqlrequests.createSimpleCharge
import com.carecloud.test_module.providers.initXavierProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by drodriguez on 2019-09-11.
 */
@RunWith(AndroidJUnit4::class)
class PMPartialMakePayment: BaseTest() {

    private val amount = 100
    private val patient = PatientData.patient3

    @Before
    override
    fun setup() {
        initXavierProvider()
        createSimpleCharge(amount, patient.id)
        super.setup()
    }

    @Test
    fun pmPartialMakePayment() {
        PracticeMainScreen()
                .pressChangeModeButton()
                .pressPatientModeButton()
                .pressLetsStartButton()
                .pressPaymentButton()
                .pressLoginButton()
                .typeUsername(PatientData.patient3.email)
                .typePassword("Test123!")
                .pressLoginButton()
                .selectFirstPayment()
                .makePartialPayment()
                .enterAmount(100)
                .payUseCreditCardOnFile(null)
    }
}