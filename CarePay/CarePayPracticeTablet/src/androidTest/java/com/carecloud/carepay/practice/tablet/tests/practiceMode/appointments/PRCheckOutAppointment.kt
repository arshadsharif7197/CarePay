package com.carecloud.carepay.practice.tablet.tests.practiceMode.appointments

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.PracticeMainScreen
import com.carecloud.carepay.practice.tablet.tests.BaseTest
import com.carecloud.test_module.graphqlrequests.checkinAppointment
import com.carecloud.test_module.graphqlrequests.createAppointment
import com.carecloud.test_module.providers.formatAppointmentTime
import com.carecloud.test_module.providers.initXavierProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by drodriguez on 2019-10-02.
 */
@RunWith(AndroidJUnit4::class)
class PRCheckOutAppointment : BaseTest() {

    var appointmentTime: String = ""

    @Before
    override
    fun setup() {
        initXavierProvider()
        val appointmentResponse  = createAppointment()
        appointmentTime = formatAppointmentTime(
                appointmentResponse.data?.createAppointment?.start_time.toString(), true)
        checkinAppointment(appointmentResponse.data?.createAppointment?.id)
        super.setup()
    }


    @Test
    fun prCheckOutAppointment() {
        PracticeMainScreen()
                .pressAppointmentsButton()
                .checkOutAppointmentAtTime(appointmentTime)
                .scheduleLater()
                .verifyAppointmentStatus("Just Checked Out")
                .goHome()
    }
}