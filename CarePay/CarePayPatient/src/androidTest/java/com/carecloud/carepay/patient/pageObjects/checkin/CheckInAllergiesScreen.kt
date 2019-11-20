package com.carecloud.carepay.patient.pageObjects.checkin

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.carepay.patient.pageObjects.appointments.AppointmentScreen
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-09-19.
 */
class CheckInAllergiesScreen: CustomViewActions() {
    fun addAllergies(): CheckInAllergiesScreen {
        click(appContext.getString(R.string.content_description_yes_button))
        type(appContext.getString(R.string.content_description_search_input_box), "a\n")
        clickOnRecyclerViewItem(appContext.getString(R.string.content_description_medications_and_allergies_list), 0)
        clickOnSpecificText("NEXT STEP")
        return this
    }

    fun skipAllergies(): CheckInIntakeFormsScreen {
        click(appContext.getString(R.string.content_description_no_button))
        return CheckInIntakeFormsScreen()
    }

    fun allergiesNextstep(): AppointmentScreen {
        clickOnSpecificText("NEXT STEP")
        return AppointmentScreen()
    }
}