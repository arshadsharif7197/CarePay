package com.carecloud.carepay.patient.pageObjects

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * @author pjohnson on 2019-09-13.
 */
class ConfirmationScreen : CustomViewActions() {

    fun confirm() {
        click(appContext.getString(R.string.content_description_confirm))
    }


}