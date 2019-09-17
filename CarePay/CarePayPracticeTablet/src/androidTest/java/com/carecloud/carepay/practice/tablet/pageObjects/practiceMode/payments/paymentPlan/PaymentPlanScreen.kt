package com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.payments.paymentPlan

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * @author pjohnson on 2019-09-10.
 */
class PaymentPlanScreen : CustomViewActions() {

    init {
        verifyViewVisible(appContext.getString(R.string.content_description_payment_plan_screen))
    }

    fun chooseItem(): PaymentPlanScreen {
        clickOnRecyclerViewItemChildrenWithId(appContext.getString(R.string.content_description_payment_plan_item_list),
                0, R.id.itemCheckBox)
        return PaymentPlanScreen()
    }

    fun typePlanName(name: String): PaymentPlanScreen {
        scrollABitDown(appContext.getString(R.string.content_description_payment_plan_scroll))
        type(appContext.getString(R.string.content_description_payment_plan_name), name, true)
        return this
    }

    fun typeNumberOfMonths(numberOfMonths: String): PaymentPlanScreen {
        scrollDown(appContext.getString(R.string.content_description_payment_plan_scroll))
        wait(milliseconds = 1000)
        type(appContext.getString(R.string.content_description_number_of_months), numberOfMonths, true)
        return PaymentPlanScreen()
    }

    fun clickCreateButton(): PaymentPlanConfirmationScreen {
        click(appContext.getString(R.string.content_description_create_payment_plan_button))
        wait(milliseconds = 4000)
        return PaymentPlanConfirmationScreen()
    }


}
