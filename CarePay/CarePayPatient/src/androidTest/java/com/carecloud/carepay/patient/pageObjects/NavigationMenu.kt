package com.carecloud.carepay.patient.pageObjects

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.carepay.patient.pageObjects.payments.PaymentsScreen
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-09-10.
 */
class NavigationMenu : CustomViewActions() {

    fun goToPayments(): PaymentsScreen {
        click(appContext.getString(R.string.content_description_payments_screen_button))
        return PaymentsScreen()
    }

}