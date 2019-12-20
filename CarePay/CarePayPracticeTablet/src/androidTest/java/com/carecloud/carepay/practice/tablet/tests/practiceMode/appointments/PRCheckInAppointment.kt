package com.carecloud.carepay.practice.tablet.tests.practiceMode.appointments

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.PracticeMainScreen
import com.carecloud.carepay.practice.tablet.tests.BaseTest
import com.carecloud.test_module.graphql.createAppointment
import com.carecloud.test_module.graphql.getBreezeToken
import com.carecloud.test_module.providers.makeRequest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by drodriguez on 2019-10-01.
 */
@RunWith(AndroidJUnit4::class)
class PMCheckInAppointment : BaseTest() {

    @Before
    override
    fun setup() {
        val response = makeRequest(getBreezeToken(appMode = "practice"))
        makeRequest(createAppointment(), authHeader = response.data?.getBreezeSessionToken?.xavier_token.toString())
        super.setup()
    }

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
                .verifyAppointmentStatus("Just Checked In")
                .goHome()
    }
}