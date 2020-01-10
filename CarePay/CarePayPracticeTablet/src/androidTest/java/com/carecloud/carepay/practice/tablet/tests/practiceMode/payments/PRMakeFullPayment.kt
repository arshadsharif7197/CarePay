package com.carecloud.carepay.practice.tablet.tests.practiceMode.payments

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
 * Created by drodriguez on 2019-09-06.
 */
@RunWith(AndroidJUnit4::class)
class PRMakeFullPayment: BaseTest() {

    private val amount = 100
    private val patient = PatientData.patient2

    @Before
    override
    fun setup() {
        initXavierProvider()
        createSimpleCharge(amount, patient.id)
        super.setup()
    }
    @Test
    fun prMakeFullPayment() {

        PracticeMainScreen()
                .pressPaymentButton()
                .searchForPatient(patient.name)
                .changeTotalBeingPaid()
                .enterAmount(amount)
//                .selectProviderForItemOnList(0)
//                .selectLocationForItemOnList(0)
                .pressPayButton()
                .selectCreditCardPayment(0)
                .pressPayButton()
    }
}