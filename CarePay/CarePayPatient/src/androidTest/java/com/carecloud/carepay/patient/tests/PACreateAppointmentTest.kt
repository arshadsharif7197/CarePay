package com.carecloud.carepay.patient.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.patient.BaseTest
import com.carecloud.carepay.patient.pageObjects.LoginScreen
import com.carecloud.carepay.patient.pageObjects.TutorialScreen
import com.carecloud.carepay.patient.pageObjects.appointments.AppointmentScreen
import com.carecloud.carepay.patient.pageObjects.appointments.appointmentTime
import com.carecloud.carepay.patient.patientPassword
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by drodriguez on 2019-09-03.
 */
@RunWith(AndroidJUnit4::class)
class PACreateAppointmentTest : BaseTest() {

    private val provider = "Barry Argentine"
    @Test
    fun paCreateAppointmentTest() {
        LoginScreen()
                .typeUser("dev_emails+automationbreeze3@carecloud.com")
                .typePassword(patientPassword)
                .pressLoginButton()
                .addNewAppointment()
                .selectProvider(provider)
                .selectVisitType()
                .selectLocation()
                .pressCheckAvailableTimes()
                .chooseAppointmentTime()
                .pressScheduleAppointment()
                .discardReviewPopup()
                .verifyAppointmentIsOnList(provider)
                .verifyAppointmentIsOnList(appointmentTime)
    }
}