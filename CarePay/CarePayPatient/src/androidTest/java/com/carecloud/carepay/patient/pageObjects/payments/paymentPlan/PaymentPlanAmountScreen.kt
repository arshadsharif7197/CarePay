package com.carecloud.carepay.patient.pageObjects.payments.paymentPlan

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.test_module.actions.CustomViewActions

/**
 * @author pjohnson on 2019-09-12.
 */
class PaymentPlanAmountScreen : CustomViewActions() {

    init {
        verifyViewVisible(appContext.getString(R.string.content_description_amount_screen))
    }

    fun typeAmount(amount: String): PaymentPlanAmountScreen {
        type(appContext.getString(R.string.content_description_enter_payment_amount), amount)
        return this
    }

    fun clickCreateButton(): PaymentPlanScreen {
        click(appContext.getString(R.string.content_description_make_partial_payment_button))
        return PaymentPlanScreen()
    }


}