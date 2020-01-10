package com.carecloud.carepay.practice.tablet.pageObjects.patientMode.checkin

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.pageObjects.patientMode.payments.PaymentDetailsDialog
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.test_module.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-10-24.
 */
class CheckInPaymentDetails: CustomViewActions() {
    fun selectPaymentOptions() : PaymentDetailsDialog {
        click(appContext.getString(R.string.content_description_payment_options_button))
        return PaymentDetailsDialog()
    }
}