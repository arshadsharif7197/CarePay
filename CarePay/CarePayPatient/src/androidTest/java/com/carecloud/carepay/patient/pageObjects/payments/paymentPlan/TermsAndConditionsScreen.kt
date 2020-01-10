package com.carecloud.carepay.patient.pageObjects.payments.paymentPlan

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.test_module.actions.CustomViewActions

/**
 * @author pjohnson on 2019-09-12.
 */
class TermsAndConditionsScreen : CustomViewActions() {

//    init {
//        verifyViewVisible(appContext.getString(R.string.content_description_terms_and_conditions_screen))
//    }

    fun acceptTermsAndConditions(): PaymentPlanConfirmationScreen {
        click(appContext.getString(R.string.content_description_create_payment_plan_button))
        return PaymentPlanConfirmationScreen()
    }
}