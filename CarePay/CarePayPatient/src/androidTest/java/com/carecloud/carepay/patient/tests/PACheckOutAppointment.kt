package com.carecloud.carepay.patient.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.patient.BaseTest
import com.carecloud.carepay.patient.pageObjects.LoginScreen
import com.carecloud.carepay.patient.pageObjects.TutorialScreen
import com.carecloud.carepay.patient.pageObjects.appointments.AppointmentScreen
import com.carecloud.carepaylibray.androidTest.graphqlrequests.checkinAppointment
import com.carecloud.carepaylibray.androidTest.graphqlrequests.createAppointment
import com.carecloud.carepaylibray.androidTest.graphqlrequests.deleteAppointment
import com.carecloud.carepaylibray.androidTest.graphqlrequests.getBreezeToken
import com.carecloud.carepaylibray.androidTest.providers.formatAppointmentTime
import com.carecloud.carepaylibray.androidTest.providers.initXavierProvider
import com.carecloud.carepaylibray.androidTest.providers.makeRequest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by drodriguez on 2019-09-17.
 */
@RunWith(AndroidJUnit4::class)
class PACheckOutAppointment : BaseTest() {

    @Before
    override
    fun setup() {
        initXavierProvider()
        val appointmentResponse  = createAppointment(47335868)
        checkinAppointment(appointmentResponse.data?.createAppointment?.id)
        super.setup()
    }

    @Test
    fun paCheckOutAppointment() {
        LoginScreen()
                .typeUser("dev_emails+qa.automationbreeze4@carecloud.com")
                .typePassword("Test123!")
                .pressLoginButton()
                .checkOutFirstAppointmentOnList(1)
                .scheduleAppointmentLater()
    }
}