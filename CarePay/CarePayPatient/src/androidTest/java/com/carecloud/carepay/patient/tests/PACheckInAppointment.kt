package com.carecloud.carepay.patient.tests

import androidx.test.runner.AndroidJUnit4
import com.carecloud.carepay.patient.BaseTest
import com.carecloud.carepay.patient.pageObjects.appointments.AppointmentScreen
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by drodriguez on 2019-09-17.
 */
@RunWith(AndroidJUnit4::class)
class PACheckInAppointment : BaseTest() {

    @Test
    fun paCheckInAppointment() {
        AppointmentScreen()
                .clickOnAppointmentOnList(1)
                .goToAddress()
                .goToDemographics()
                .goToNextstep()
                .goToNextstep()
                .goToNextstep()

    }
}