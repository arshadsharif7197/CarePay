package com.carecloud.carepay.patient.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.patient.BaseTest
import com.carecloud.carepay.patient.pageObjects.appointments.AppointmentScreen
import com.carecloud.carepay.patient.pageObjects.checkin.CheckInAllergiesScreen
import com.carecloud.carepay.patient.pageObjects.checkin.CheckInMedicationsScreen
import com.carecloud.carepay.patient.pageObjects.checkin.demographics.CheckInDemogAddressScreen
import com.carecloud.carepay.patient.pageObjects.checkin.demographics.CheckInDemogDemographicsScreen
import com.carecloud.carepay.patient.pageObjects.payments.PaymentLineItemsDetails
import com.carecloud.carepaylibray.androidTest.graphqlrequests.changePaymentSetting
import com.carecloud.carepaylibray.androidTest.graphqlrequests.createAppointment
import com.carecloud.carepaylibray.androidTest.graphqlrequests.createSimpleCharge
import com.carecloud.carepaylibray.androidTest.graphqlrequests.getBreezeToken
import com.carecloud.carepaylibray.androidTest.providers.formatAppointmentTime
import com.carecloud.carepaylibray.androidTest.providers.initXavierProvider
import com.carecloud.carepaylibray.androidTest.providers.makeRequest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by drodriguez on 2019-10-24.
 */
@RunWith(AndroidJUnit4::class)
class PACheckInAppointmentWithPayment: BaseTest() {

    private lateinit var apptTime: String

    @Before
    override
    fun setup() {
        initXavierProvider()
        val appointment = createAppointment()
        apptTime = formatAppointmentTime(appointment.data?.createAppointment?.start_time.toString())
        changePaymentSetting("checkin")
        createSimpleCharge()
        super.setup()
    }

    @Test
    fun paCheckInAppointmentWithPayment() {
        AppointmentScreen()
                .checkInAppointmentOnListAtTime(apptTime)
                .personalInfoNextStep(CheckInDemogAddressScreen())
                .addressNextStep(CheckInDemogDemographicsScreen())
                .demographicsNextStep(CheckInMedicationsScreen())
                .medicationsNextstep(CheckInAllergiesScreen())
                .allergiesNextstep(PaymentLineItemsDetails())
                .selectPaymentOptions()
                .makeFullPayment()
                .payUseCreditCardOnFile(AppointmentScreen())
                .discardReviewPopup()
    }

    @After
    override
    fun tearDown() {
        // TODO: clean up appointment
        super.tearDown()
    }
}