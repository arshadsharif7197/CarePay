package com.carecloud.carepay.patient.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.patient.BaseTest
import com.carecloud.carepay.patient.pageObjects.LoginScreen
import com.carecloud.carepay.patient.pageObjects.TutorialScreen
import com.carecloud.carepay.patient.pageObjects.appointments.AppointmentScreen
import com.carecloud.carepay.patient.pageObjects.payments.PaymentsScreen
import com.carecloud.test_module.graphqlrequests.createSimpleCharge
import com.carecloud.test_module.providers.initXavierProvider
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by drodriguez on 2019-09-10.
 */

@RunWith(AndroidJUnit4::class)
class PAMakePartialPaymentTest : BaseTest() {

    @Before
    override
    fun setup() {
        initXavierProvider()
        createSimpleCharge(20)
        super.setup()
    }

    @Test
    fun paMakePartialPaymentTest() {
        LoginScreen()
                .typeUser("dev_emails+qa.androidbreeze2@carecloud.com")
                .typePassword("Test123!")
                .pressLoginButton()
                .openNavigationDrawer()
                .goToPayments()
                .makePaymentFor(0)
                .selectPaymentOptions()
                .makePartialPayment(20)
                .payUseCreditCardOnFile(PaymentsScreen())
                .discardReviewPopup()
    }

}