package com.carecloud.carepay.practice.tablet.tests.practiceMode.appointments

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.practice.tablet.pageObjects.shared.appointments.appointmentTime
import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.PracticeMainScreen
import com.carecloud.carepay.practice.tablet.tests.BaseTest
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Created by drodriguez on 08/12/19.
 */

@RunWith(AndroidJUnit4::class)
class PRAddAppointmentTest : BaseTest() {

    @Test
    fun prAddAppointmentPracticeTest() {

        PracticeMainScreen()
                .pressAppointmentsButton()
                .pressAddAppointmentButton()
                .searchForPatient()
                .selectProvider()
                .selectVisitType()
                .selectLocation()
                .pressCheckAvailableTimes()
                .chooseAppointmentTime()
                .pressScheduleAppointment()
    }
}