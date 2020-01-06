package com.carecloud.carepay.patient.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.patient.BaseTest
import com.carecloud.carepay.patient.pageObjects.LoginScreen
import com.carecloud.carepay.patient.pageObjects.TutorialScreen
import com.carecloud.carepay.patient.pageObjects.appointments.AppointmentScreen
import com.carecloud.carepay.patient.pageObjects.checkin.CheckInAllergiesScreen
import com.carecloud.carepay.patient.pageObjects.checkin.CheckInIntakeFormsScreen
import com.carecloud.carepay.patient.pageObjects.checkin.CheckInMedicationsScreen
import com.carecloud.carepay.patient.pageObjects.checkin.demographics.CheckInDemogAddressScreen
import com.carecloud.carepay.patient.pageObjects.checkin.demographics.CheckInDemogDemographicsScreen
import com.carecloud.test_module.data.PatientData
import com.carecloud.test_module.graphqlrequests.*
import com.carecloud.test_module.providers.formatAppointmentTime
import com.carecloud.test_module.providers.initXavierProvider
import com.carecloud.test_module.providers.makeRequest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by drodriguez on 2019-09-17.
 */
@RunWith(AndroidJUnit4::class)
class PACheckInAppointmentWithForm : BaseTest() {

    private lateinit var apptTime: String
    private var apptId: Int? = null
    private val patient = PatientData.patient12

    @Before
    override
    fun setup() {
        initXavierProvider()
        val appointment = createAppointment(patient.id)
        apptTime = formatAppointmentTime(appointment.data?.createAppointment?.start_time.toString())
        apptId = appointment.data?.createAppointment?.id
        changePaymentSetting("neither")
        changePatientFormSettings(true)
        super.setup()
    }

    @Test
    fun paCheckInAppointmentWithForm() {
        LoginScreen()
                .typeUser(patient.email)
                .typePassword("Test123!")
                .pressLoginButton()
                .checkInAppointmentOnListAtTime(apptTime)
                .personalInfoNextStep(CheckInDemogAddressScreen())
                .addressNextStep(CheckInDemogDemographicsScreen())
                .demographicsNextStep(CheckInIntakeFormsScreen())
                .scrollToBottomOfIntake()
                .goToNextStep(CheckInMedicationsScreen())
                .medicationsNextstep(CheckInAllergiesScreen())
                .allergiesNextstep(AppointmentScreen())
                .discardReviewPopup()
    }

    @After
    override
    fun tearDown() {
        changePatientFormSettings(false)
        deleteAppointment(apptId)
        super.tearDown()
    }

}