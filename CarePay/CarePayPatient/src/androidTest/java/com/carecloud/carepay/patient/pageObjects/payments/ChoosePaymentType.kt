package com.carecloud.carepay.patient.pageObjects.payments

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.carepay.patient.pageObjects.payments.paymentPlan.PaymentPlanAmountScreen
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-09-10.
 */
class ChoosePaymentType : CustomViewActions() {
    fun makePartialPayment(paymentAmount: Int): PaymentMethod {
        wait(milliseconds = 1000)
        click(appContext.getString(R.string.content_description_make_partial_payment_button))
        wait(milliseconds = 1000)
        type(appContext.getString(R.string.content_description_enter_payment_amount), paymentAmount.toString())
        click(appContext.getString(R.string.content_description_make_partial_payment_button))
        return PaymentMethod()
    }

    fun makeFullPaymemt(): PaymentMethod {
        wait(milliseconds = 1000)
        click(appContext.getString(R.string.content_description_pay_total_amount_button))
        return PaymentMethod()
    }

    fun chooseCreatePaymentPlan(): PaymentPlanAmountScreen {
        wait(milliseconds = 1000)
        click(appContext.getString(R.string.content_description_create_payment_plan_button))
        return PaymentPlanAmountScreen()
    }
}