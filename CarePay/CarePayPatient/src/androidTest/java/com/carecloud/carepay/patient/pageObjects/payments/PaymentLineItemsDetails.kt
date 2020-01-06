package com.carecloud.carepay.patient.pageObjects.payments

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-10-24.
 */
class PaymentLineItemsDetails : CustomViewActions() {
    fun selectPaymentOptions() : ChoosePaymentType {
        click(appContext.getString(R.string.content_description_payment_options_button))
        return ChoosePaymentType()
    }
}