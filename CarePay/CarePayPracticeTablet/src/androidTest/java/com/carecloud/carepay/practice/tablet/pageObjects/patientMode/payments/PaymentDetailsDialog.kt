package com.carecloud.carepay.practice.tablet.pageObjects.patientMode.payments

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.pageObjects.patientMode.payments.paymentPlan.PaymentPlanAmountScreen
import com.carecloud.carepay.practice.tablet.pageObjects.shared.payments.EnterAmountDialog
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.test_module.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-09-11.
 */
class PaymentDetailsDialog : CustomViewActions() {

    fun makePartialPayment(): EnterAmountDialog<PaymentMethodDialog> {
        click(appContext.getString(R.string.content_description_pay_partial_button))
        return EnterAmountDialog(PaymentMethodDialog())
    }

    fun makeFullPayment(): PaymentMethodDialog {
        click(appContext.getString(R.string.content_description_pay_total_button))
        return PaymentMethodDialog()
    }

    fun clickCreateButton(): PaymentPlanAmountScreen {
        click(appContext.getString(R.string.content_description_create_payment_plan_button))
        return PaymentPlanAmountScreen()
    }
}