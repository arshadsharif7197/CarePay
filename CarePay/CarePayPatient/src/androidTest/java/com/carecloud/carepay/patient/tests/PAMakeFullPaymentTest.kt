package com.carecloud.carepay.patient.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.patient.BaseTest
import com.carecloud.carepay.patient.pageObjects.appointments.AppointmentScreen
import com.carecloud.test_module.graphql.createSimpleCharge
import com.carecloud.test_module.graphql.getBreezeToken
import com.carecloud.test_module.providers.makeRequest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by drodriguez on 2019-09-12.
 */
@RunWith(AndroidJUnit4::class)
class PAMakeFullPaymentTest: BaseTest() {

    @Before
    override
    fun setup() {
        val response = makeRequest(getBreezeToken(appMode = "practice"))
        makeRequest(createSimpleCharge(), authHeader = response.data?.getBreezeSessionToken?.xavier_token.toString())
        super.setup()
    }

    @Test
    fun paMakeFullPaymentTest() {
        AppointmentScreen()
                .openNavigationDrawer()
                .goToPayments()
                .makePaymentFor(0)
                .makeFullPaymemt()
                .payUseCreditCardOnFile()
    }
}