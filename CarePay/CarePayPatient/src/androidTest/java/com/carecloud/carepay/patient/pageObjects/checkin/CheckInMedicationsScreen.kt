package com.carecloud.carepay.patient.pageObjects.checkin

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.test_module.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-09-19.
 */
class CheckInMedicationsScreen: CustomViewActions() {
    fun addMedications(): CheckInMedicationsScreen {
        click(appContext.getString(R.string.content_description_yes_button))
        type(appContext.getString(R.string.content_description_search_input_box), "a\n")
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_medications_and_allergies_list), 0)
        clickOnSpecificText("NEXT STEP")
        return this
    }

    fun skipMedications(): CheckInMedicationsScreen {
        click(appContext.getString(R.string.content_description_no_button))
        return this
    }

    fun <T>medicationsNextstep(next: T): T {
        clickOnSpecificText("NEXT STEP")
        return next
    }
}