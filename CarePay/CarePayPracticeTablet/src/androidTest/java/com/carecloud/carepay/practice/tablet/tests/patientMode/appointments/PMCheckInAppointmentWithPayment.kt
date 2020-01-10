package com.carecloud.carepay.practice.tablet.tests.patientMode.appointments

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.practice.tablet.pageObjects.patientMode.checkin.*
import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.PracticeMainScreen
import com.carecloud.carepay.practice.tablet.tests.BaseTest
import com.carecloud.carepay.practice.tablet.tests.patientPassword
import com.carecloud.test_module.data.PatientData
import com.carecloud.test_module.graphqlrequests.*
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
class PMCheckInAppointmentWithPayment : BaseTest() {

    lateinit var appointmentTime: String
    private var appointmentId: Int? = null
    private val patient = PatientData.patient4

    @Before
    override
    fun setup() {
        initXavierProvider()
        val apptResponse = createAppointment(patient.id)
        appointmentTime = formatAppointmentTime(apptResponse.data?.createAppointment?.start_time.toString())
        appointmentId = apptResponse.data?.createAppointment?.id
        changePaymentSetting("checkin")
        changePatientFormSettings(false)
        createSimpleCharge(50, patient.id)
        super.setup()
    }

    @Test
    fun pmCheckInAppointmentWithPayment() {
        PracticeMainScreen()
                .pressChangeModeButton()
                .pressPatientModeButton()
                .pressLetsStartButton()
                .pressCheckInButton()
                .pressLoginButton()
                .typeUsername(patient.email)
                .typePassword(patientPassword)
                .pressLoginButton()
                .checkInAppointment(CheckInPersonalInfo(), appointmentTime)
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
        changePaymentSetting("neither")
        super.tearDown()
    }
}