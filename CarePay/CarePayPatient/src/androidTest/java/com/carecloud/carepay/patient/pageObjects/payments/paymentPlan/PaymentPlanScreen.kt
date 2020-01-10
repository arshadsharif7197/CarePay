package com.carecloud.carepay.patient.pageObjects.payments.paymentPlan

import com.carecloud.carepay.patient.R
import com.carecloud.carepay.patient.appContext
import com.carecloud.carepay.patient.pageObjects.payments.PaymentMethod
import com.carecloud.test_module.actions.CustomViewActions

/**
 * @author pjohnson on 2019-09-12.
 */
class PaymentPlanScreen : CustomViewActions() {

    fun typePlanName(name: String): PaymentPlanScreen {
        type(appContext.getString(R.string.content_description_payment_plan_name), name, true)
        return this
    }

    fun typeNumberOfMonths(numberOfMonths: String): PaymentPlanScreen {
        type(appContext.getString(R.string.content_description_number_of_months), numberOfMonths, true)
        return this
    }

    fun clickCreateButton(): PaymentMethod {
        click(appContext.getString(R.string.content_description_create_payment_plan_button))
        return PaymentMethod()
    }
}