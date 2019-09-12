package com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.payments

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-09-10.
 */
class ChoosePaymentMethodDialog: CustomViewActions() {
    fun selectCreditCardPayment(): ChoosePaymentMethodDialog {
        clickOnItemOnList(appContext.getString(R.string.content_description_payment_types_list), 1)
        return this
    }

    fun pressPayButton() {
        click(appContext.getString(R.string.content_description_pay_button))
        click(appContext.getString(R.string.content_description_ok_button))
    }
}