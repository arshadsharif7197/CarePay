package com.carecloud.carepay.patient.pageObjects.payments

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-09-10.
 */
class PaymentsScreen: CustomViewActions() {
    fun makePaymentFor(position: Int): ChoosePaymentType {
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_payments_list), position)
        click(appContext.getString(R.string.content_description_payment_options_button))
        return ChoosePaymentType()
    }
}