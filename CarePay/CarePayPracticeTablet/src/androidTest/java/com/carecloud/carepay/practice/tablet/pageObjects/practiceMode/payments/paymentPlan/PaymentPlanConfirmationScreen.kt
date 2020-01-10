package com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.payments.paymentPlan

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.payments.PaymentsScreen
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.test_module.actions.CustomViewActions

/**
 * @author pjohnson on 2019-09-10.
 */
class PaymentPlanConfirmationScreen : CustomViewActions() {

    fun clickOkButton() : PaymentsScreen{
        click(appContext.getString(R.string.content_description_ok_button))
        return PaymentsScreen()
    }
}