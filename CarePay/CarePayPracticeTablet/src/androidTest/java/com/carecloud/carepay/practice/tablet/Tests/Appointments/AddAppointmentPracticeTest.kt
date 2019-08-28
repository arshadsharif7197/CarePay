package com.carecloud.carepay.practice.tablet.Tests.Appointments

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.practice.tablet.PageObjects.PracticeMainScreen
import com.carecloud.carepay.practice.tablet.Tests.BaseTest
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Created by drodriguez on 08/12/19.
 */

@RunWith(AndroidJUnit4::class)
class AddAppointmentPracticeTest : BaseTest() {

    @Test
    fun addAppointmentPracticeTest() {

        PracticeMainScreen().pressAppointmentsButton()
                .pressAddAppointmentButton()
                .searchForPatient()
                .chooseProvider()
                .chooseVisitType()
                .chooseLocation()
                .pressCheckAvailableTimes()
                .chooseAppointmentTime()
                .pressScheduleAppointment()
    }
}