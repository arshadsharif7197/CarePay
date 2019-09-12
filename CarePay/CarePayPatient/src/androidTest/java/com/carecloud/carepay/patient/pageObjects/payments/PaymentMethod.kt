package com.carecloud.carepay.patient.pageObjects.payments

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-09-11.
 */
class PaymentMethod : CustomViewActions() {

    fun payUseCreditCardOnFile(): PaymentsScreen {
        clickOnItemOnList(appContext.getString(R.string.content_description_payment_methods), 1)
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_credit_cards_list), 0)
        click(appContext.getString(R.string.content_description_pay_button))
        click(appContext.getString(R.string.content_description_ok_button))
        return PaymentsScreen()
    }

}