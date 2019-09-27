package com.carecloud.carepay.patient.pageObjects.checkin.demographics

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.carepay.patient.pageObjects.checkin.CheckInMedicationsScreen
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * Created by drodriguez on 2019-09-17.
 */
open class CheckInDemographicsScreen: CustomViewActions() {
    fun goToAddress(): CheckInDemogAddressScreen {
        click(appContext.getString(R.string.content_description_next_button))
        return CheckInDemogAddressScreen()
    }

    fun goToDemographics(): CheckInDemogDemographicsScreen {
        click(appContext.getString(R.string.content_description_next_button))
        return CheckInDemogDemographicsScreen()
    }

    fun goToIdentity(): CheckInDemogIdentityScreen {
        click(appContext.getString(R.string.content_description_next_button))
        return CheckInDemogIdentityScreen()
    }

    fun goToInsurance(): CheckInDemogInsuranceScreen {
        click(appContext.getString(R.string.content_description_next_button))
        return CheckInDemogInsuranceScreen()
    }

    fun finishDemographics(): CheckInMedicationsScreen {
        click(appContext.getString(R.string.content_description_next_button))
        return CheckInMedicationsScreen()
    }
}