package com.carecloud.carepay.practice.tablet.pageObjects.patientMode.payments

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

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