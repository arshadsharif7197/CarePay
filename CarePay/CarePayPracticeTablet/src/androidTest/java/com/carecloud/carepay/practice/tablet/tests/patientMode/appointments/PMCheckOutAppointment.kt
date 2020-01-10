package com.carecloud.carepay.practice.tablet.tests.patientMode.appointments

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.PracticeMainScreen
import com.carecloud.carepay.practice.tablet.tests.BaseTest
import com.carecloud.carepay.practice.tablet.tests.patientPassword
import com.carecloud.test_module.data.PatientData
import com.carecloud.test_module.graphqlrequests.checkinAppointment
import com.carecloud.test_module.graphqlrequests.createAppointment
import com.carecloud.test_module.graphqlrequests.deleteAppointment
import com.carecloud.test_module.providers.initXavierProvider
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by drodriguez on 2019-10-02.
 */
@RunWith(AndroidJUnit4::class)
class PMCheckOutAppointment : BaseTest() {

    private val patient = PatientData.patient5
    private var apptId: Int? = null

    @Before
    override
    fun setup() {
        initXavierProvider()
        val appointmentResponse = createAppointment(patient.id)
        apptId = appointmentResponse.data?.createAppointment?.id
        checkinAppointment(apptId)
        super.setup()
    }

    @Test
    fun pmCheckOutAppointment() {
        PracticeMainScreen()
                .pressChangeModeButton()
                .pressPatientModeButton()
                .pressLetsStartButton()
                .pressCheckOutButton()
                .pressLoginButton()
                .typeUsername(patient.email)
                .typePassword(patientPassword)
                .pressLoginButton()
                .scheduleLater()
                .verifyAppointmentStatus("Just Checked Out")
                .goHome()
    }

    @After
    override
    fun tearDown() {
        deleteAppointment(apptId)
        super.tearDown()
    }
}