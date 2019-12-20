package com.carecloud.carepay.practice.tablet.tests.practiceMode.appointments

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.PracticeMainScreen
import com.carecloud.carepay.practice.tablet.tests.BaseTest
import com.carecloud.test_module.graphql.checkinAppointment
import com.carecloud.test_module.graphql.createAppointment
import com.carecloud.test_module.graphql.getBreezeToken
import com.carecloud.test_module.providers.makeRequest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by drodriguez on 2019-10-02.
 */
@RunWith(AndroidJUnit4::class)
class PRCheckOutAppointment : BaseTest() {

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
    fun pmCheckOutAppointment() {
        PracticeMainScreen()
                .pressAppointmentsButton()
                .checkOutFirstAppointmentOnList()
                .scheduleLater()
                .verifyAppointmentStatus("Just Checked Out")
                .goHome()
    }
}