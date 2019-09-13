package com.carecloud.carepay.patient.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.patient.BaseTest
import com.carecloud.carepay.patient.pageObjects.AppointmentScreen
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author pjohnson on 2019-09-12.
 */
@RunWith(AndroidJUnit4::class)
class PaymentPlanTest : BaseTest() {

    private val paymentPlanName = "Automated test"
    private val paymentPlanAmount = "100"

//    @Test
//    fun createPaymentPlanTest() {
//        AppointmentScreen()
//                .openNavigationDrawer()
//                .goToPayments()
//                .chooseBalance(0)
//                .chooseCreatePaymentPlan()
//                .typeAmount("100")
//                .clickCreateButton()
//                .typePlanName("Automated test")
//                .typeNumberOfMonths("5")
//                .clickCreateButton()
//                .chooseCreditCardMethod(0)
//                .chooseCreditCard()
//                .acceptTermsAndConditions()
//                .clickOk()
////                .verifyPaymentPlanIsOnList(paymentPlanName)
////                .verifyPaymentPlanIsOnList(paymentPlanAmount)
//    }

    @Test
    fun deletePaymentPlanTest() {
        AppointmentScreen()
                .openNavigationDrawer()
                .goToPayments()
                .choosePaymentPlan("")

    }
}