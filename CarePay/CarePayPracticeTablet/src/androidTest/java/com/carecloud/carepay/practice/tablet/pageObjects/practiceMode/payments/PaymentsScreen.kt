package com.carecloud.carepay.practice.tablet.pageObjects.practiceMode.payments

import com.carecloud.carepay.practice.tablet.R
import com.carecloud.carepay.practice.tablet.tests.appContext
import com.carecloud.test_module.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-09-06.
 */
class PaymentsScreen : CustomViewActions() {

    fun searchForPatient(patientName: String): PaymentsDialog {
        click(appContext.getString(R.string.content_description_find_a_patient))
        type(appContext.getString(R.string.content_description_patient_search), patientName)
        wait(appContext.getString(R.string.content_description_patient_list), milliseconds = 50000)
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_patient_list), 0)
        return PaymentsDialog()
    }

}