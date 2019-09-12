package com.carecloud.carepay.patient.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.patient.BaseTest
import com.carecloud.carepay.patient.pageObjects.AppointmentScreen
import com.carecloud.carepay.patient.pageObjects.LoginScreen
import com.carecloud.carepay.patient.pageObjects.appointments.appointmentTime
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by drodriguez on 2019-09-03.
 */
@RunWith(AndroidJUnit4::class)
class PACreateAppointmentTest : BaseTest() {

    val provider = "Pamela Banes"
    @Test
    fun paCreateAppointmentTest() {
        AppointmentScreen()
                .addNewAppointment()
                .switchBE("Lisa J. Learn D.O., PA")
                .selectProvider(provider)
                .selectVisitType()
                .selectLocation()
                .pressCheckAvailableTimes()
                .chooseAppointmentTime()
                .pressScheduleAppointment()
                .verifyAppointmentIsOnList(provider)
                .verifyAppointmentIsOnList(appointmentTime)
    }

}