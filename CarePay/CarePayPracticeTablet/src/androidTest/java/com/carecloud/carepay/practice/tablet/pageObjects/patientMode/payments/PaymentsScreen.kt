package com.carecloud.carepay.practice.tablet.pageObjects.patientMode.payments

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-09-11.
 */
class PaymentsScreen : CustomViewActions() {

    fun selectFirstPayment(): PaymentDetailsDialog {
        clickOnRecyclerViewItemChildren(appContext.getString(R.string.content_description_payments_list), 0, "Details")
        return PaymentDetailsDialog()
    }

}