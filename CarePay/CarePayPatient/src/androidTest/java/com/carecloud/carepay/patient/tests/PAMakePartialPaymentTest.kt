package com.carecloud.carepay.patient.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.patient.BaseTest
import com.carecloud.carepay.patient.pageObjects.LoginScreen
import com.carecloud.carepay.patient.pageObjects.TutorialScreen
import com.carecloud.carepay.patient.pageObjects.appointments.AppointmentScreen
import com.carecloud.carepay.patient.pageObjects.payments.PaymentsScreen
import com.carecloud.carepay.patient.patientPassword
import com.carecloud.carepaylibray.androidTest.data.PatientData
import com.carecloud.carepaylibray.androidTest.graphqlrequests.createSimpleCharge
import com.carecloud.carepaylibray.androidTest.providers.initXavierProvider
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by drodriguez on 2019-09-10.
 */

@RunWith(AndroidJUnit4::class)
class PAMakePartialPaymentTest : BaseTest() {

    private val patient = PatientData.patient14

    @Before
    override
    fun setup() {
        initXavierProvider()
        createSimpleCharge(20, patient.id)
        super.setup()
    }

    @Test
    fun paMakePartialPaymentTest() {
        LoginScreen()
                .typeUser(patient.email)
                .typePassword(patientPassword)
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