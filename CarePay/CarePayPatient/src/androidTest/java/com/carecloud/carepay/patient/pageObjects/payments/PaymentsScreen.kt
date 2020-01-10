package com.carecloud.carepay.patient.pageObjects.payments

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.carepay.patient.pageObjects.payments.paymentPlan.PaymentPlanDetailScreen
import com.carecloud.test_module.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-09-10.
 */
class PaymentsScreen : CustomViewActions() {
    fun chooseBalance(position: Int): ChoosePaymentType {
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_payments_list), position)
        click(appContext.getString(R.string.content_description_payment_options_button))
        return ChoosePaymentType()
    }

    fun makePaymentFor(position: Int): PaymentLineItemsDetails {
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_payments_list), position)
        return PaymentLineItemsDetails()
    }

    fun verifyPaymentPlanIsOnList(textMatch: String): PaymentsScreen {
        verifyItemInRecyclerView(appContext.getString(R.string.content_description_payments_list), textMatch)
        return this
    }

    fun choosePaymentPlan(paymentPlanName: String) : PaymentPlanDetailScreen {
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_payments_list), textMatch = paymentPlanName)
        return PaymentPlanDetailScreen()
    }

    fun discardReviewPopup(): PaymentsScreen {
        click(appContext.getString(R.string.content_description_not_now_button))
        return this
    }
}