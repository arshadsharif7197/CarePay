package com.carecloud.carepay.practice.tablet.tests.practiceMode.appointments

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.practice.tablet.pageObjects.patientMode.checkin.*
import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.PracticeMainScreen
import com.carecloud.carepay.practice.tablet.tests.BaseTest
import com.carecloud.carepaylibray.androidTest.graphqlrequests.*
import com.carecloud.carepaylibray.androidTest.providers.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by drodriguez on 2019-10-01.
 */
@RunWith(AndroidJUnit4::class)
class PMCheckInAppointmentWithPayment : BaseTest() {

    lateinit var appointmentTime : String

    @Before
    override
    fun setup() {
        initXavierProvider()
        val apptResponse = createAppointment()
        appointmentTime = formatAppointmentTime(apptResponse.data?.createAppointment?.start_time.toString())
        changePaymentSetting("checkin")
        createSimpleCharge()
        super.setup()
    }

    @Test
    fun pmCheckInAppointmentWithPayment() {
        PracticeMainScreen()
                .pressAppointmentsButton()
                .checkInAppointmentAtTime(appointmentTime)
                .personalInfoNextStep(CheckInAddress())
                .addressNextStep(CheckInDemographics())
                .demographicsNextStep(CheckInMedications())
                .medicationsNextStep(CheckInAllergies())
                .allergiesNextStep(CheckInPaymentDetails())
                .selectPaymentOptions()
                .makeFullPayment()
                .payUseCreditCardOnFile(CheckInOutConfirmation())
                .verifyAppointmentStatus("Just Checked In")
                .goHome()
    }
}