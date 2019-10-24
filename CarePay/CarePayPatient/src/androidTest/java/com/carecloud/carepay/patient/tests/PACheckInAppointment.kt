package com.carecloud.carepay.patient.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.patient.BaseTest
import com.carecloud.carepay.patient.pageObjects.appointments.AppointmentScreen
import com.carecloud.carepay.patient.pageObjects.checkin.CheckInAllergiesScreen
import com.carecloud.carepay.patient.pageObjects.checkin.CheckInMedicationsScreen
import com.carecloud.carepay.patient.pageObjects.checkin.demographics.CheckInDemogAddressScreen
import com.carecloud.carepay.patient.pageObjects.checkin.demographics.CheckInDemogDemographicsScreen
import com.carecloud.carepaylibray.androidTest.graphql.changePaymentSetting
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
class PACheckInAppointment : BaseTest() {

    @Before
    override
    fun setup() {
        val response = makeRequest(getBreezeToken(appMode = "practice"))
        val tokens = response.data?.getBreezeSessionToken
        makeRequest(createAppointment(), authHeader = tokens?.xavier_token.toString())
        makeRequest(changePaymentSetting("neither"),
                tokens?.cognito_token?.authenticationToken.toString())
        super.setup()
    }

    @Test
    fun paCheckInAppointment() {
        AppointmentScreen()
                .checkInFirstAppointmentOnList(1)
                .personalInfoNextStep(CheckInDemogAddressScreen())
                .addressNextStep(CheckInDemogDemographicsScreen())
                .demographicsNextStep(CheckInMedicationsScreen())
                .medicationsNextstep(CheckInAllergiesScreen())
                .allergiesNextstep(AppointmentScreen())
    }
}