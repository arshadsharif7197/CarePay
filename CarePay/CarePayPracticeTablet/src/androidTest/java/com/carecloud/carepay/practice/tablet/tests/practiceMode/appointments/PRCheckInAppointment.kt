package com.carecloud.carepay.practice.tablet.tests.practiceMode.appointments

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.practice.tablet.pageObjects.patientMode.checkin.*
import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.PracticeMainScreen
import com.carecloud.carepay.practice.tablet.tests.BaseTest
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
 * Created by drodriguez on 2019-10-01.
 */
@RunWith(AndroidJUnit4::class)
class PRCheckInAppointment : BaseTest() {

    lateinit var appointmentTime: String
    private var appointmentId: Int? = null

    @Before
    override
    fun setup() {
        initXavierProvider()
        val apptResponse = createAppointment()
        appointmentTime = formatAppointmentTime(
                apptResponse.data?.createAppointment?.start_time.toString(), true)
        appointmentId = apptResponse.data?.createAppointment?.id
        changePaymentSetting("neither")
        super.setup()
    }

    @Test
    fun prCheckInAppointment() {
        PracticeMainScreen()
                .pressAppointmentsButton()
                .checkInAppointmentAtTime(appointmentTime)
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