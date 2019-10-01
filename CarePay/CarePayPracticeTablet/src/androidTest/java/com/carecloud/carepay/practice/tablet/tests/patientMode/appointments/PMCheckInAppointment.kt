package com.carecloud.carepay.practice.tablet.tests.patientMode.appointments

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.PracticeMainScreen
import com.carecloud.carepay.practice.tablet.tests.BaseTest
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by drodriguez on 2019-09-27.
 */
@RunWith(AndroidJUnit4::class)
class PMCheckInAppointment : BaseTest() {

    @Test
    fun pmCheckInAppointment() {
        PracticeMainScreen()
                .pressChangeModeButton()
                .pressPatientModeButton()
                .pressLetsStartButton()
                .pressCheckInButton()
                .pressLoginButton()
                .typeUsername("dev_emails+qa.androidbreeze1@carecloud.com")
                .typePassword("Test123!")
                .pressLoginButton()
                .checkInAppointment()
                .personalInfoNextStep()
                .addressNextStep()
                .demographicsNextStep()
                .medicationsNextStep()
                .allergiesNextStep()
                .goHome()
    }
}