package com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.payments.paymentPlan

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.test_module.actions.CustomViewActions

/**
 * @author pjohnson on 2019-09-10.
 */
class PaymentPlanDashboardScreen : CustomViewActions() {

    fun pressCreateNewPaymentPlanButton(): PaymentPlanScreen<PaymentPlanConfirmationScreen> {
        click(appContext.getString(R.string.content_description_create_payment_plan_button))
        return PaymentPlanScreen(PaymentPlanConfirmationScreen())
    }

    fun choosePaymentPlan(paymentPlanName: String): PaymentPlanDetailScreen {
        clickOnRecyclerViewItemChildrenWithId(appContext.getString(R.string.content_description_payments_list),
                0, R.id.detailsButton)
        return PaymentPlanDetailScreen()
    }


}