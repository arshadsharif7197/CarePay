package com.carecloud.carepay.practice.tablet.tests.practiceMode.payments

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.PracticeMainScreen
import com.carecloud.carepay.practice.tablet.tests.BaseTest
import com.carecloud.carepaylibray.androidTest.graphqlrequests.changePaymentSetting
import com.carecloud.carepaylibray.androidTest.graphqlrequests.createAppointment
import com.carecloud.carepaylibray.androidTest.graphqlrequests.createSimpleCharge
import com.carecloud.carepaylibray.androidTest.providers.formatAppointmentTime
import com.carecloud.carepaylibray.androidTest.providers.initXavierProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by drodriguez on 2019-09-06.
 */
@RunWith(AndroidJUnit4::class)
class PRMakeFullPayment: BaseTest() {

    @Before
    override
    fun setup() {
        initXavierProvider()
        //TODO: find patient id and add to simple charge
        createSimpleCharge(100, 47336319)
        super.setup()
    }
    @Test
    fun prMakeFullPayment() {

        PracticeMainScreen()
                .pressPaymentButton()
                .searchForPatient("first android")
                .changeTotalBeingPaid()
                .enterAmount("20")
                .selectProviderForItemOnList(0)
                .selectLocationForItemOnList(0)
                .pressPayButton()
                .selectCreditCardPayment(1)
                .pressPayButton()
    }
}