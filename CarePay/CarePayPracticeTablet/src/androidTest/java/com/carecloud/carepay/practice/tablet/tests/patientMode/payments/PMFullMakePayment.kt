package com.carecloud.carepay.practice.tablet.tests.patientMode.payments

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.PracticeMainScreen
import com.carecloud.carepay.practice.tablet.tests.BaseTest
import com.carecloud.carepaylibray.androidTest.data.PatientData
import com.carecloud.carepaylibray.androidTest.graphqlrequests.createSimpleCharge
import com.carecloud.carepaylibray.androidTest.graphqlrequests.getBreezeToken
import com.carecloud.carepaylibray.androidTest.providers.initXavierProvider
import com.carecloud.carepaylibray.androidTest.providers.makeRequest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by drodriguez on 2019-09-12.
 */
@RunWith(AndroidJUnit4::class)
class PMFullMakePayment: BaseTest() {

    private val patient = PatientData.patient7

    @Before
    override
    fun setup() {
        initXavierProvider()
        createSimpleCharge(100, patient.id)
        super.setup()
    }

    @Test
    fun pmFullMakePayment() {
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
                .makeFullPayment()
                .payUseCreditCardOnFile(null)
    }
}