package com.carecloud.carepay.patient.pageObjects.payments.paymentPlan

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.carepay.patient.pageObjects.ConfirmationScreen
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * @author pjohnson on 2019-09-10.
 */
class PaymentPlanEditScreen : CustomViewActions() {

    init {
        verifyViewVisible(appContext.getString(R.string.content_description_payment_plan_screen))
    }

    fun deletePaymentPlan(): ConfirmationScreen {
        scrollDown(appContext.getString(R.string.content_description_payment_plan_screen))
        click(appContext.getString(R.string.content_description_payment_plan_delete_button))
        return ConfirmationScreen()
    }

    fun editNumberOfMonths(numberOfMonths: String): PaymentPlanEditScreen {
        replaceText(appContext.getString(R.string.content_description_number_of_months), numberOfMonths, true)
        return this
    }

    fun saveChanges():PaymentPlanConfirmationScreen {
        scrollDown(appContext.getString(R.string.content_description_payment_plan_screen))
        click(appContext.getString(R.string.content_description_save_payment_plan_changes))
        return PaymentPlanConfirmationScreen()
    }

}