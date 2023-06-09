package com.carecloud.carepay.patient.pageObjects.payments

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.carepay.patient.pageObjects.payments.paymentPlan.TermsAndConditionsScreen
import com.carecloud.test_module.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-09-11.
 */
class PaymentMethod : CustomViewActions() {

    fun <T> payUseCreditCardOnFile(next: T): T {
        clickOnItemOnList(appContext.getString(R.string.content_description_payment_methods), 0)
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_credit_cards_list), 0)
        click(appContext.getString(R.string.content_description_pay_button))
        click(appContext.getString(R.string.content_description_ok_button))
        return next
    }

    fun chooseCreditCardMethod(paymentMethodPosition: Int): CreditCardScreen<TermsAndConditionsScreen> {
        clickOnItemOnList(appContext.getString(R.string.content_description_payment_methods), paymentMethodPosition)
        return CreditCardScreen(screenAfterChoosingCard = TermsAndConditionsScreen())
    }

}