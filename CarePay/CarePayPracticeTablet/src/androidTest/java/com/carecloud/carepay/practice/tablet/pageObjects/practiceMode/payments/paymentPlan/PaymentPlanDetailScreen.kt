package com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.payments.paymentPlan

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * @author pjohnson on 2019-09-16.
 */
class PaymentPlanDetailScreen : CustomViewActions() {

    init{
        verifyViewVisible(appContext.getString(R.string.content_description_payment_plan_detail_screen))
    }

    fun editPaymentPlan() : PaymentPlanEditScreen {
        click(appContext.getString(R.string.content_description_payment_plan_edit_button))
        return PaymentPlanEditScreen()
    }


}