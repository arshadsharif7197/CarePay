package com.carecloud.carepay.patient.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.patient.BaseTest
import com.carecloud.carepay.patient.pageObjects.appointments.AppointmentScreen
import com.carecloud.carepaylibray.androidTest.graphql.checkinAppointment
import com.carecloud.carepaylibray.androidTest.graphql.createAppointment
import com.carecloud.carepaylibray.androidTest.graphql.getBreezeToken
import com.carecloud.carepaylibray.androidTest.providers.makeRequest
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
        val response = makeRequest(getBreezeToken(appMode = "practice"))
        val authToken = response.data?.getBreezeSessionToken?.xavier_token.toString()
        val appointmentResponse  = makeRequest(createAppointment(),
                authHeader = authToken)
        makeRequest(checkinAppointment(appointmentResponse.data?.createAppointment?.id), authToken)
        super.setup()
    }

    @Test
    fun paCheckOutAppointment() {
        AppointmentScreen()
                .checkOutFirstAppointmentOnList(1)
                .scheduleAppointmentLater()
    }
}