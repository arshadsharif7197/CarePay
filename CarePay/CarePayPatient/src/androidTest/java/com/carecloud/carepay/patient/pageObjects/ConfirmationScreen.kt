package com.carecloud.carepay.patient.pageObjects

import com.carecloud.carepay.patient.pageObjects.payments.PaymentsScreen
import com.carecloud.test_module.actions.CustomViewActions

/**
 * @author pjohnson on 2019-09-10.
 */
class ConfirmationScreen : CustomViewActions() {

    fun confirm(): PaymentsScreen {
        clickOnSpecificText("YES")
        return PaymentsScreen()
    }

}