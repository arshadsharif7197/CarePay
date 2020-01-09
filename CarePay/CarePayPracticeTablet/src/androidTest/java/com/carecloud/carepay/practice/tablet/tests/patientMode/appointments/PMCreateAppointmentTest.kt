package com.carecloud.carepay.practice.tablet.tests.patientMode.appointments

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.PracticeMainScreen
import com.carecloud.carepay.practice.tablet.tests.BaseTest
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by drodriguez on 2019-08-28.
 */

@RunWith(AndroidJUnit4::class)
class PMCreateAppointmentTest : BaseTest() {

    @Test
    fun pmCreateAppointmentTest() {
        PracticeMainScreen()
                .pressChangeModeButton()
                .pressPatientModeButton()
                .pressLetsStartButton()
                .pressAppointmentButton()
                .pressLoginButton()
                .typeUsername("dev_emails+qa.androidbreeze2@carecloud.com")
                .typePassword("Test123!")
                .pressLoginButton()
                .selectProvider()
                .selectVisitType()
                .selectLocation()
                .pressCheckAvailableTimes()
                .chooseAppointmentTime()
                .pressScheduleAppointment()

    }
}