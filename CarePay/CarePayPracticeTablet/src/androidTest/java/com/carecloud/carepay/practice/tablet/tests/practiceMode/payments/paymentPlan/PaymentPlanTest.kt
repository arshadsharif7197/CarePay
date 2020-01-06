package com.carecloud.carepay.practice.tablet.tests.practiceMode.payments.paymentPlan

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.PracticeMainScreen
import com.carecloud.carepay.practice.tablet.tests.BaseTest
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * @author pjohnson on 2019-09-10.
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class PaymentPlanTest : BaseTest() {

    private val paymentPlanName = "Automated test"
    private val numberOfMonths = "5"

    @Test
    fun a_createPaymentPlanTest() {
        PracticeMainScreen()
                .pressPaymentButton()
                .searchForPatient("shop")
                .openPaymentPlansDashboard()
                .pressCreateNewPaymentPlanButton()
                .chooseItem()
                .typePlanName(paymentPlanName)
                .typeNumberOfMonths(numberOfMonths)
                .clickCreateButton()
                .clickOkButton()
    }

    @Test
    fun b_editPaymentPlanTest() {
        PracticeMainScreen()
                .pressPaymentButton()
                .searchForPatient("shop")
                .openPaymentPlansDashboard()
                .choosePaymentPlan(paymentPlanName)
                .editPaymentPlan()
                .editNumberOfMonths("3")
                .saveChanges()
                .clickOkButton()
    }

    @Test
    fun c_deletePaymentPlanTest() {
        PracticeMainScreen()
                .pressPaymentButton()
                .searchForPatient("shop")
                .openPaymentPlansDashboard()
                .choosePaymentPlan(paymentPlanName)
                .editPaymentPlan()
                .deletePaymentPlan()
                .confirm()
    }
}