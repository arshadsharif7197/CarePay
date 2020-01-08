package com.carecloud.carepay.practice.tablet.tests.practiceMode.payments.paymentPlan

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.PracticeMainScreen
import com.carecloud.carepay.practice.tablet.tests.BaseTest
import com.carecloud.carepaylibray.androidTest.data.PatientData
import com.carecloud.carepaylibray.androidTest.graphqlrequests.createSimpleCharge
import com.carecloud.carepaylibray.androidTest.graphqlrequests.makePayment
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
    private val patient = PatientData.patient10

    @Test
    fun a_prCreatePaymentPlanTest() {
        PracticeMainScreen()
                .pressPaymentButton()
                .searchForPatient(patient.name)
                .openPaymentPlansDashboard()
                .pressCreateNewPaymentPlanButton()
                .chooseItem()
                .typePlanName(paymentPlanName)
                .typeNumberOfMonths(numberOfMonths)
                .clickCreateButton()
                .clickOkButton()
    }

    @Test
    fun b_prEditPaymentPlanTest() {
        PracticeMainScreen()
                .pressPaymentButton()
                .searchForPatient(patient.name)
                .openPaymentPlansDashboard()
                .choosePaymentPlan(paymentPlanName)
                .editPaymentPlan()
                .editNumberOfMonths("3")
                .saveChanges()
                .clickOkButton()
    }

    @Test
    fun c_prDeletePaymentPlanTest() {
        PracticeMainScreen()
                .pressPaymentButton()
                .searchForPatient(patient.name)
                .openPaymentPlansDashboard()
                .choosePaymentPlan(paymentPlanName)
                .editPaymentPlan()
                .deletePaymentPlan()
                .confirm()
    }
}