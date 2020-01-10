package com.carecloud.carepay.practice.tablet.pageObjects.patientMode.payments.paymentPlan

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.pageObjects.patientMode.payments.PaymentMethodDialog
import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.payments.paymentPlan.PaymentPlanScreen
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.test_module.actions.CustomViewActions

/**
 * @author pjohnson on 2019-09-17.
 */
class PaymentPlanAmountScreen : CustomViewActions() {

    init {
        verifyViewVisible(appContext.getString(R.string.content_description_amount_screen))
    }

    fun typeAmount(amount: String): PaymentPlanAmountScreen {
        click(appContext.getString(R.string.content_description_key_one))
        click(appContext.getString(R.string.content_description_key_five))
        click(appContext.getString(R.string.content_description_key_zero),true)
        return this
    }

    fun clickCreateButton(): PaymentPlanScreen<PaymentMethodDialog> {
        click(appContext.getString(R.string.content_description_apply_amount_button))
        return PaymentPlanScreen(PaymentMethodDialog())
    }
}