package com.carecloud.carepay.patient.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.patient.BaseTest
import com.carecloud.carepay.patient.pageObjects.LoginScreen
import com.carecloud.carepay.patient.pageObjects.payments.PaymentsScreen
import com.carecloud.test_module.graphqlrequests.createSimpleCharge
import com.carecloud.test_module.providers.initXavierProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by drodriguez on 2019-09-12.
 */
@RunWith(AndroidJUnit4::class)
class PAMakeFullPaymentTest : BaseTest() {

    @Before
    override
    fun setup() {
        initXavierProvider()
        createSimpleCharge()
        super.setup()
    }

    @Test
    fun paMakeFullPaymentTest() {
        LoginScreen()
                .typeUser("dev_emails+qa.androidbreeze2@carecloud.com")
                .typePassword("Test123!")
                .pressLoginButton()
                .openNavigationDrawer()
                .goToPayments()
                .makePaymentFor(0)
                .selectPaymentOptions()
                .makeFullPayment()
                .payUseCreditCardOnFile(PaymentsScreen())
                .discardReviewPopup()
    }
}