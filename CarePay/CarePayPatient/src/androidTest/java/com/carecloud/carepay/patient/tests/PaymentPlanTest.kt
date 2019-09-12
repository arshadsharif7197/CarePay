package com.carecloud.carepay.patient.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.patient.BaseTest
import com.carecloud.carepay.patient.pageObjects.AppointmentScreen
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author pjohnson on 2019-09-12.
 */
@RunWith(AndroidJUnit4::class)
class PaymentPlanTest : BaseTest() {

    @Test
    fun createPaymentPlanTest() {
        AppointmentScreen()
                .openPaymentScreen()
                .createPaymentPlan()
    }
}