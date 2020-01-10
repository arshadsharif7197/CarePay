package com.carecloud.carepay.patient.pageObjects.payments

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.test_module.actions.CustomViewActions

/**
 * @author pjohnson on 2019-09-12.
 */
class CreditCardScreen<T>(private val screenAfterChoosingCard: T) : CustomViewActions() {

    init {
        verifyViewVisible(appContext.getString(R.string.content_description_credit_cards_screen))
    }

    fun chooseCreditCard(): T {
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_credit_cards_list), 0)
        click(appContext.getString(R.string.content_description_pay_button))
        return screenAfterChoosingCard
    }


}