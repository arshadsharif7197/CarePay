package com.carecloud.carepay.patient.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.patient.BaseTest
import com.carecloud.carepay.patient.pageObjects.LoginScreen
import com.carecloud.carepay.patient.pageObjects.TutorialScreen
import com.carecloud.carepay.patient.pageObjects.appointments.AppointmentScreen
import com.carecloud.test_module.graphqlrequests.changePaymentPlanSetting
import com.carecloud.test_module.graphqlrequests.createSimpleCharge
import com.carecloud.test_module.graphqlrequests.makePayment
import com.carecloud.test_module.providers.initXavierProvider
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * @author pjohnson on 2019-09-12.
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class PAPaymentPlanTest : BaseTest() {

    private val paymentPlanName = "Automated test"
    private val paymentPlanAmount = "100"

    @Test
    fun a_createPaymentPlanTest() {
        initXavierProvider()
        createSimpleCharge()
        changePaymentPlanSetting(true)

        LoginScreen()
                .typeUser("dev_emails+qa.androidbreeze2@carecloud.com")
                .typePassword("Test123!")
                .pressLoginButton()
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
                .discardReviewPopup()
    }

    @Test
    fun b_editPaymentPlanTest() {
        LoginScreen()
                .typeUser("dev_emails+qa.androidbreeze2@carecloud.com")
                .typePassword("Test123!")
                .pressLoginButton()
                .openNavigationDrawer()
                .goToPayments()
                .choosePaymentPlan(paymentPlanName)
                .editPaymentPlan()
                .editNumberOfMonths("3")
                .saveChanges()
                .clickOk()
                .discardReviewPopup()
    }

    @Test
    fun c_deletePaymentPlanTest() {
        LoginScreen()
                .typeUser("dev_emails+qa.androidbreeze2@carecloud.com")
                .typePassword("Test123!")
                .pressLoginButton()
                .openNavigationDrawer()
                .goToPayments()
                .choosePaymentPlan(paymentPlanName)
                .editPaymentPlan()
                .deletePaymentPlan()
                .confirm()

        initXavierProvider("patient")
        makePayment()
    }
}