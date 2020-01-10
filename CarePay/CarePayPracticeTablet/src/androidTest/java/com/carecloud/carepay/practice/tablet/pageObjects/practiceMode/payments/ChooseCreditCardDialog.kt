package com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.payments

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.test_module.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-09-10.
 */
class ChooseCreditCardDialog: CustomViewActions() {
    fun selectCreditCardAtPosition(position: Int) {
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_credit_cards_list), 0)

    }
}