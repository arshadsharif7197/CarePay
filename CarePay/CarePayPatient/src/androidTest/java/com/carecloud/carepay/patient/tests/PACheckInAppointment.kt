package com.carecloud.carepay.patient.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.patient.BaseTest
import com.carecloud.carepay.patient.pageObjects.LoginScreen
import com.carecloud.carepay.patient.pageObjects.TutorialScreen
import com.carecloud.carepay.patient.pageObjects.appointments.AppointmentScreen
import com.carecloud.carepay.patient.pageObjects.checkin.CheckInAllergiesScreen
import com.carecloud.carepay.patient.pageObjects.checkin.CheckInMedicationsScreen
import com.carecloud.carepay.patient.pageObjects.checkin.demographics.CheckInDemogAddressScreen
import com.carecloud.carepay.patient.pageObjects.checkin.demographics.CheckInDemogDemographicsScreen
import com.carecloud.carepay.patient.patientPassword
import com.carecloud.carepaylibray.androidTest.graphqlrequests.changePaymentSetting
import com.carecloud.carepaylibray.androidTest.graphqlrequests.createAppointment
import com.carecloud.carepaylibray.androidTest.graphqlrequests.deleteAppointment
import com.carecloud.carepaylibray.androidTest.providers.formatAppointmentTime
import com.carecloud.carepaylibray.androidTest.providers.initXavierProvider
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by drodriguez on 2019-09-17.
 */
@RunWith(AndroidJUnit4::class)
class PACheckInAppointment : BaseTest() {

    private lateinit var apptTime: String
    private var apptId: Int? = null

    @Before
    override
    fun setup() {
        initXavierProvider()
        val appointment = createAppointment()
        apptTime = formatAppointmentTime(appointment.data?.createAppointment?.start_time.toString())
        apptId = appointment.data?.createAppointment?.id
        changePaymentSetting("neither")
        super.setup()
    }

    @Test
    fun paCheckInAppointment() {
        LoginScreen()
                .typeUser("dev_emails+qa.androidbreeze2@carecloud.com")
                .typePassword(patientPassword)
                .pressLoginButton()
                .checkInAppointmentOnListAtTime(apptTime)
                .personalInfoNextStep(CheckInDemogAddressScreen())
                .addressNextStep(CheckInDemogDemographicsScreen())
                .demographicsNextStep(CheckInMedicationsScreen())
                .medicationsNextstep(CheckInAllergiesScreen())
                .allergiesNextstep(AppointmentScreen())
                .discardReviewPopup()
    }

    @After
    override
    fun tearDown() {
        deleteAppointment(apptId)
        super.tearDown()
    }

}