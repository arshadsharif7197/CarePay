package com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.payments

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.pageObjects.shared.payments.EnterAmountDialog
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-09-06.
 */
class PaymentsDialog: CustomViewActions() {
    fun changeTotalBeingPaid(): EnterAmountDialog<PaymentsDialog> {
        click(appContext.getString(R.string.content_description_total_amount_being_paid))
        return EnterAmountDialog(PaymentsDialog())
    }

    fun selectProviderForItemOnList(position: Int): PaymentsDialog {
        clickOnRecyclerViewItemChildren(appContext.getString(R.string.content_description_patient_balances_list),
                position,"Choose Provider")
        clickInPopupWindow("Pamela Banes")
        return this
    }

    fun selectLocationForItemOnList(position: Int): PaymentsDialog{
        clickOnRecyclerViewItemChildren(appContext.getString(R.string.content_description_patient_balances_list),
                position,"Choose Location")
        clickInPopupWindow("HOME")
        return this
    }

    fun pressPayButton(): ChoosePaymentMethodDialog {
        click(appContext.getString(R.string.content_description_pay_button))
        return ChoosePaymentMethodDialog()
    }
}