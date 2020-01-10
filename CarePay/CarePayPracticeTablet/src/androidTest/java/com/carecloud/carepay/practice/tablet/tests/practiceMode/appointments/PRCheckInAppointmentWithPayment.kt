package com.carecloud.carepay.practice.tablet.tests.practiceMode.appointments

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.practice.tablet.pageObjects.patientMode.checkin.*
import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.PracticeMainScreen
import com.carecloud.carepay.practice.tablet.tests.BaseTest
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
 * Created by drodriguez on 2019-10-01.
 */
@RunWith(AndroidJUnit4::class)
class PRCheckInAppointmentWithPayment : BaseTest() {

    lateinit var appointmentTime: String
    private var appointmentId: Int? = null
    private val patient = PatientData.patient13

    @Before
    override
    fun setup() {
        initXavierProvider()
        val apptResponse = createAppointment(patient.id)
        appointmentTime = formatAppointmentTime(apptResponse.data?.createAppointment?.start_time.toString(), true)
        appointmentId = apptResponse.data?.createAppointment?.id
        changePaymentSetting("checkin")
        createSimpleCharge(100, patient.id)
        super.setup()
    }

    @Test
    fun prCheckInAppointmentWithPayment() {
        PracticeMainScreen()
                .pressAppointmentsButton()
                .checkInAppointmentAtTime(appointmentTime)
                .personalInfoNextStep(CheckInAddress())
                .addressNextStep(CheckInDemographics())
                .demographicsNextStep(CheckInMedications())
                .medicationsNextStep(CheckInAllergies())
                .allergiesNextStep(CheckInPaymentDetails())
                .selectPaymentOptions()
                .makeFullPayment()
                .payUseCreditCardOnFile(CheckInOutConfirmation())
                .verifyAppointmentStatus("Just Checked In")
                .goHome()
    }

    @After
    override
    fun tearDown() {
        deleteAppointment(appointmentId)
        super.tearDown()
    }
}