package com.carecloud.carepay.patient.pageObjects.payments

import com.carecloud.carepay.patient.R
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * @author pjohnson on 2019-09-12.
 */
class PaymentScreen : CustomViewActions() {

    init {
        verifyViewVisible(R.id.payments_pager_layout)
    }

    fun createPaymentPlan(){
        clickOnRecyclerViewItem(R.id.payment_list_recycler, 1)
        clickOnSpecificId(R.id.bottom_sheet)
        clickOnSpecificId(R.id.paymentPlanContainer)
    }
}