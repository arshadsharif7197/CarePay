package com.carecloud.carepay.practice.tablet.tests.patientMode.appointments

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.practice.tablet.pageObjects.patientMode.checkin.*
import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.PracticeMainScreen
import com.carecloud.carepay.practice.tablet.tests.BaseTest
import com.carecloud.carepay.practice.tablet.tests.patientPassword
import com.carecloud.test_module.data.PatientData
import com.carecloud.test_module.graphqlrequests.changePatientFormSettings
import com.carecloud.test_module.graphqlrequests.changePaymentSetting
import com.carecloud.test_module.graphqlrequests.createAppointment
import com.carecloud.test_module.graphqlrequests.deleteAppointment
import com.carecloud.test_module.providers.formatAppointmentTime
import com.carecloud.test_module.providers.initXavierProvider
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by drodriguez on 2019-09-27.
 */
@RunWith(AndroidJUnit4::class)
class PMCheckInAppointment : BaseTest() {

    private lateinit var apptTime: String
    private var appointmentId: Int? = null
    private val patient = PatientData.patient18

    @Before
    override
    fun setup() {
        initXavierProvider()
        val apptResponse = createAppointment(patient.id)
        apptTime = formatAppointmentTime(apptResponse.data?.createAppointment?.start_time.toString())
        appointmentId = apptResponse.data?.createAppointment?.id
        changePaymentSetting("neither")
        changePatientFormSettings(false)
        super.setup()
    }

    @Test
    fun pmCheckInAppointment() {
        PracticeMainScreen()
                .pressChangeModeButton()
                .pressPatientModeButton()
                .pressLetsStartButton()
                .pressCheckInButton()
                .pressLoginButton()
                .typeUsername(patient.email)
                .typePassword(patientPassword)
                .pressLoginButton()
                .checkInAppointment(CheckInPersonalInfo(), apptTime)
                .personalInfoNextStep(CheckInAddress())
                .addressNextStep(CheckInDemographics())
                .demographicsNextStep(CheckInMedications())
                .medicationsNextStep(CheckInAllergies())
                .allergiesNextStep(CheckInOutConfirmation())
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