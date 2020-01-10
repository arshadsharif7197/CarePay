package com.carecloud.carepay.patient.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.patient.BaseTest
import com.carecloud.carepay.patient.pageObjects.LoginScreen
import com.carecloud.carepay.patient.pageObjects.appointments.AppointmentScreen
import com.carecloud.carepay.patient.pageObjects.checkin.CheckInAllergiesScreen
import com.carecloud.carepay.patient.pageObjects.checkin.CheckInMedicationsScreen
import com.carecloud.carepay.patient.pageObjects.checkin.demographics.CheckInDemogAddressScreen
import com.carecloud.carepay.patient.pageObjects.checkin.demographics.CheckInDemogDemographicsScreen
import com.carecloud.carepay.patient.pageObjects.payments.PaymentLineItemsDetails
import com.carecloud.carepay.patient.patientPassword
import com.carecloud.test_module.data.PatientData
import com.carecloud.test_module.graphqlrequests.changePaymentSetting
import com.carecloud.test_module.graphqlrequests.createAppointment
import com.carecloud.test_module.graphqlrequests.createSimpleCharge
import com.carecloud.test_module.graphqlrequests.deleteAppointment
import com.carecloud.test_module.providers.formatAppointmentTime
import com.carecloud.test_module.providers.initXavierProvider
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by drodriguez on 2019-10-24.
 */
@RunWith(AndroidJUnit4::class)
class PACheckInAppointmentWithPayment : BaseTest() {

    private lateinit var apptTime: String
    private var apptId: Int? = null
    private val patient = PatientData.patient16

    @Before
    override
    fun setup() {
        initXavierProvider()
        val appointment = createAppointment(patient.id)
        apptTime = formatAppointmentTime(appointment.data?.createAppointment?.start_time.toString())
        apptId = appointment.data?.createAppointment?.id
        changePaymentSetting("checkin")
        createSimpleCharge(100, patient.id)
        super.setup()
    }

    @Test
    fun paCheckInAppointmentWithPayment() {
        LoginScreen()
                .typeUser(patient.email)
                .typePassword(patientPassword)
                .pressLoginButton()
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
        deleteAppointment(apptId)
        super.tearDown()
    }
}