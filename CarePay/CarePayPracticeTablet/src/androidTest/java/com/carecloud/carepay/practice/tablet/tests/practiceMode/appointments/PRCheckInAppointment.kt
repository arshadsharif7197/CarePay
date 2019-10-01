package com.carecloud.carepay.practice.tablet.tests.practiceMode.appointments

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.PracticeMainScreen
import com.carecloud.carepay.practice.tablet.tests.BaseTest
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by drodriguez on 2019-10-01.
 */
@RunWith(AndroidJUnit4::class)
class PMCheckInAppointment : BaseTest() {

    @Test
    fun pmCheckInAppointment() {
        PracticeMainScreen()
                .pressAppointmentsButton()
                .checkInFirstAppointmentOnList()
                .personalInfoNextStep()
                .addressNextStep()
                .demographicsNextStep()
                .medicationsNextStep()
                .allergiesNextStep()
                .goHome()
    }
}