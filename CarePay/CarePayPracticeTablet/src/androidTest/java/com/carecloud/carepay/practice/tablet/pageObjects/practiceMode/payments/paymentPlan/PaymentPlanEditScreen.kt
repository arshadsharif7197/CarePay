package com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.payments.paymentPlan

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.pageObjects.ConfirmationScreen
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * @author pjohnson on 2019-09-16.
 */
class PaymentPlanEditScreen : CustomViewActions() {

    init {
        verifyViewVisible(appContext.getString(R.string.content_description_payment_plan_screen))
    }

    fun deletePaymentPlan(): ConfirmationScreen {
        click(appContext.getString(R.string.content_description_payment_plan_delete_button))
        return ConfirmationScreen()
    }

    fun editNumberOfMonths(numberOfMonths: String): PaymentPlanEditScreen {
        scrollDown(appContext.getString(R.string.content_description_payment_plan_scroll))
        wait(milliseconds = 1000)
        type(appContext.getString(R.string.content_description_number_of_months), numberOfMonths, true)
        return PaymentPlanEditScreen()
    }

    fun saveChanges(): PaymentPlanConfirmationScreen {
        click(appContext.getString(R.string.content_description_payment_plan_edit_button))
        return PaymentPlanConfirmationScreen()
    }
}