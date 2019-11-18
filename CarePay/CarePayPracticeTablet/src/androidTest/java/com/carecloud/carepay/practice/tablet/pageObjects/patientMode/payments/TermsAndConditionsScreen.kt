package com.carecloud.carepay.practice.tablet.pageObjects.patientMode.payments

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.payments.paymentPlan.PaymentPlanConfirmationScreen
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * @author pjohnson on 2019-09-12.
 */
class TermsAndConditionsScreen : CustomViewActions() {

//    init {
//        verifyViewVisible(appContext.getString(R.string.content_description_terms_and_conditions_screen))
//    }

    fun acceptTermsAndConditions(): PaymentPlanConfirmationScreen {
        click(appContext.getString(R.string.content_description_create_payment_plan_button))
        wait(milliseconds = 5000)
        return PaymentPlanConfirmationScreen()
    }
}