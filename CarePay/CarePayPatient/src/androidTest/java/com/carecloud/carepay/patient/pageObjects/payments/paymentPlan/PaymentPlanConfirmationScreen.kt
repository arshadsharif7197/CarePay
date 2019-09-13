package com.carecloud.carepay.patient.pageObjects.payments.paymentPlan

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.carepay.patient.pageObjects.payments.PaymentsScreen
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * @author pjohnson on 2019-09-13.
 */
class PaymentPlanConfirmationScreen : CustomViewActions() {

    init {
        verifyViewVisible(appContext.getString(R.string.content_description_payment_plan_confirmation_screen))
    }

    fun clickOk() : PaymentsScreen{
        click(appContext.getString(R.string.content_description_ok_button))
        return PaymentsScreen()
    }

}