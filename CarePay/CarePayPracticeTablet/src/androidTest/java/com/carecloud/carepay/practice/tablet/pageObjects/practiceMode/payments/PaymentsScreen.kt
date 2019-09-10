package com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.payments

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-09-06.
 */
class PaymentsScreen: CustomViewActions() {
    fun searchForPatient() : PaymentsDialog {
        click(appContext.getString(R.string.content_description_find_a_patient))
        type(appContext.getString(R.string.content_description_patient_search), "qa\n")
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_patient_list), 0)
        return PaymentsDialog()
    }
}