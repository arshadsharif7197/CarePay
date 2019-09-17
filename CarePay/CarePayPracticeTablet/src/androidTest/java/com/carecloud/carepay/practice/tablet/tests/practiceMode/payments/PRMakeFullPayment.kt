package com.carecloud.carepay.practice.tablet.tests.practiceMode.payments

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.PracticeMainScreen
import com.carecloud.carepay.practice.tablet.tests.BaseTest
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by drodriguez on 2019-09-06.
 */
@RunWith(AndroidJUnit4::class)
class PRMakeFullPayment: BaseTest() {

    @Test
    fun prMakeFullPayment() {

        PracticeMainScreen()
                .pressPaymentButton()
                .searchForPatient("qa\n")
                .changeTotalBeingPaid()
                .enterAmount("20")
                .selectProviderForItemOnList(0)
                .selectLocationForItemOnList(0)
                .pressPayButton()
                .selectCreditCardPayment()
                .pressPayButton()
    }
}