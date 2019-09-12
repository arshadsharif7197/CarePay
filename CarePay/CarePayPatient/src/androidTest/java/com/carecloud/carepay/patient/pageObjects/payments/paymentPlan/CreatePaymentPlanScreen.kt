package com.carecloud.carepay.patient.pageObjects.payments.paymentPlan

import com.carecloud.carepay.patient.pageObjects.payments.PaymentMethod
import com.carecloud.carepaylibray.androidTest.actions.CustomViewActions

/**
 * @author pjohnson on 2019-09-12.
 */
class CreatePaymentPlanScreen : CustomViewActions() {

    init {
        verifyViewVisible("")
    }

    fun typePlanName(name:String): CreatePaymentPlanScreen{
        type("", name )
        return this
    }

    fun typeNumberOfMonths(numberOfMonths:String): CreatePaymentPlanScreen{
        type("", numberOfMonths )
        return this
    }

    fun clickCreateButton(): PaymentMethod{
        click("")
        return PaymentMethod()
    }
}