package com.carecloud.carepay.patient.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.patient.BaseTest
import com.carecloud.carepay.patient.pageObjects.AppointmentScreen
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * @author pjohnson on 2019-09-12.
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class PaymentPlanTest : BaseTest() {

    private val paymentPlanName = "Automated test"
    private val paymentPlanAmount = "100"

    @Test
    fun a_createPaymentPlanTest() {
        AppointmentScreen()
                .openNavigationDrawer()
                .goToPayments()
                .chooseBalance(0)
                .chooseCreatePaymentPlan()
                .typeAmount(paymentPlanAmount)
                .clickCreateButton()
                .typePlanName(paymentPlanName)
                .typeNumberOfMonths("5")
                .clickCreateButton()
                .chooseCreditCardMethod(0)
                .chooseCreditCard()
                .acceptTermsAndConditions()
                .clickOk()
//                .verifyPaymentPlanIsOnList(paymentPlanName)
//                .verifyPaymentPlanIsOnList(paymentPlanAmount)
    }

    @Test
    fun b_editPaymentPlanTest() {
        AppointmentScreen()
                .openNavigationDrawer()
                .goToPayments()
                .choosePaymentPlan(paymentPlanName)
                .editPaymentPlan()
                .editNumberOfMonths("3")
                .saveChanges()
                .clickOk()
    }

    @Test
    fun c_deletePaymentPlanTest() {
        AppointmentScreen()
                .openNavigationDrawer()
                .goToPayments()
                .choosePaymentPlan(paymentPlanName)
                .editPaymentPlan()
                .deletePaymentPlan()
                .confirm()

    }
}