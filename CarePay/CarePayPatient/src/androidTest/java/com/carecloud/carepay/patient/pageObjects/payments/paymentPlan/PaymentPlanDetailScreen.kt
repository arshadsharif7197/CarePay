package com.carecloud.carepay.patient.pageObjects.payments.paymentPlan

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * @author pjohnson on 2019-09-10.
 */
class PaymentPlanDetailScreen : CustomViewActions() {

    init{
        verifyViewVisible(appContext.getString(R.string.content_description_payment_plan_screen))
    }

    fun editPaymentPlan() : PaymentPlanEditScreen {
        click(appContext.getString(R.string.content_description_payment_plan_edit_button))
        return PaymentPlanEditScreen()
    }


}
