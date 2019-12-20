package com.carecloud.carepay.patient.pageObjects

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.test_module.actions.CustomViewActions

/**
 * @author pjohnson on 2019-09-10.
 */
class ConfirmationScreen : CustomViewActions() {

    fun confirm() {
        click(appContext.getString(R.string.content_description_confirm))
    }

}