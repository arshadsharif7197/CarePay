package com.carecloud.carepay.practice.tablet.pageObjects.shared.payments

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.test_module.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-09-06.
 */
class EnterAmountDialog<T>(private val nextScreen: T): CustomViewActions() {
    fun enterAmount(amount: Int): T {
        amount.toString().forEach { n ->
            clickOnSpecificText(n.toString())
        }
        click(appContext.getString(R.string.content_description_apply_amount_button))
        return nextScreen
    }
}