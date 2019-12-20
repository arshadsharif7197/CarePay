package com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.payments.paymentPlan

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.test_module.actions.CustomViewActions

/**
 * @author pjohnson on 2019-09-10.
 */
class PaymentPlanScreen <T>(private val screenAfterChoosingCard: T) : CustomViewActions(){

    init {
        verifyViewVisible(appContext.getString(R.string.content_description_payment_plan_screen))
    }

    fun chooseItem(): PaymentPlanScreen<T> {
        clickOnRecyclerViewItemChildrenWithId(appContext.getString(R.string.content_description_payment_plan_item_list),
                0, R.id.itemCheckBox)
        return this
    }

    fun typePlanName(name: String): PaymentPlanScreen<T> {
        scrollABitDown(appContext.getString(R.string.content_description_payment_plan_scroll))
        type(appContext.getString(R.string.content_description_payment_plan_name), name, true)
        return this
    }

    fun typeNumberOfMonths(numberOfMonths: String): PaymentPlanScreen<T> {
        scrollDown(appContext.getString(R.string.content_description_payment_plan_scroll))
        wait(milliseconds = 1000)
        type(appContext.getString(R.string.content_description_number_of_months), numberOfMonths, true)
        return this
    }

    fun clickCreateButton(): T {
        click(appContext.getString(R.string.content_description_create_payment_plan_button))
        wait(milliseconds = 4000)
        return screenAfterChoosingCard
    }


}
