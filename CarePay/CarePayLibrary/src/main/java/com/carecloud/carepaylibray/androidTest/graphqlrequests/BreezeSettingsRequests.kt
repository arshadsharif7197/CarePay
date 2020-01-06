package com.carecloud.carepaylibray.androidTest.graphqlrequests

import com.carecloud.carepaylibray.androidTest.graphqldatamodels.Response
import com.carecloud.carepaylibray.androidTest.providers.*

/**
 * Created by drodriguez on 2019-10-24.
 */

fun changePaymentSetting(location: String): Response {
    return makeRequest(formatRequest("""
        mutation {
          changePaymentSettings(input: { setWorkflowPlacement: "$location" }
            practiceId: "54a08c18-a22f-44e1-9864-25f79995d71c"
          )
        }
    """), cognitoToken)
}

fun changePaymentPlanSetting(state: Boolean): Response {
    return makeRequest(formatRequest("""
        mutation {
          changePaymentSettings(input: { enablePaymentPlans: $state }
            practiceId: "54a08c18-a22f-44e1-9864-25f79995d71c"
          )
        }
    """), cognitoToken)
}

fun changePatientFormSettings(enabled: Boolean): Response {
    return makeRequest(formatRequest("""
        mutation {
          changePatientFormSettings(
            practiceId: "54a08c18-a22f-44e1-9864-25f79995d71c"
            enableForms: $enabled
            formNames: ["Automation Form"]
          )
        }
    """), cognitoToken)
}
