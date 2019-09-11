package com.carecloud.carepay.patient.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.patient.BaseTest
import com.carecloud.carepay.patient.pageObjects.AppointmentScreen

import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by drodriguez on 2019-09-10.
 */

@RunWith(AndroidJUnit4::class)
class PAMakePartialPaymentTest : BaseTest() {

    @Test
    fun paMakePartialPaymentTest() {
        AppointmentScreen()
                .openNavigationDrawer()
                .goToPayments()
                .makePaymentFor(0)
                .makePartialPayment(20)
                .payUseCreditCardOnFile()
    }

}