package com.carecloud.carepay.practice.tablet.pageObjects.patientMode.payments

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-09-12.
 */
class PaymentMethodDialog : CustomViewActions() {

    fun <T>payUseCreditCardOnFile(next: T):  T{
        clickOnItemOnList(appContext.getString(R.string.content_description_payment_types_list), 0)
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_credit_cards_list), 0)
        click(appContext.getString(R.string.content_description_pay_button))
        click(appContext.getString(R.string.content_description_ok_button))
        return next
    }

    fun chooseCreditCardMethod(paymentMethodPosition: Int): CreditCardScreen<TermsAndConditionsScreen> {
        clickOnItemOnList(appContext.getString(R.string.content_description_payment_types_list), paymentMethodPosition)
        return CreditCardScreen(screenAfterChoosingCard = TermsAndConditionsScreen())
    }
}