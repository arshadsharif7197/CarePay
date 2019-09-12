package com.carecloud.carepay.practice.tablet.pageObjects.shared.payments

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.payments.PaymentsDialog
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-09-06.
 */
class EnterAmountDialog: CustomViewActions() {
    fun enterAmount(amount: String): PaymentsDialog {
        amount.forEach { n ->
            clickOnSpecificText(n.toString())
        }
        click(appContext.getString(R.string.content_description_apply_amount_button))
        return PaymentsDialog()
    }
}