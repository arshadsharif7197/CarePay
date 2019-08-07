package com.carecloud.carepay.practice.tablet.Tests.Appointments

import androidx.test.ext.junit.runners.AndroidJUnit4

import com.carecloud.carepay.practice.tablet.PageObjects.PracticeMainScreen

import org.junit.Test
import org.junit.runner.RunWith

import com.carecloud.carepay.practice.tablet.Tests.BaseTest

/**
 * Created by drodriguez on 08/12/19.
 */

@RunWith(AndroidJUnit4::class)
class AddAppointmentPracticeTest : BaseTest() {
    @Test fun AddAppointmentPracticeTest() {

        PracticeMainScreen()
                .pressAppointmentsButton()
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