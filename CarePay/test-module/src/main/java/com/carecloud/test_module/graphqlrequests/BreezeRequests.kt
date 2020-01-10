package com.carecloud.test_module.graphqlrequests

import com.carecloud.test_module.graphqldatamodels.Response
import com.carecloud.test_module.providers.formatRequest
import com.carecloud.test_module.providers.makeRequest
import com.carecloud.test_module.providers.cognitoToken

/**
 * Created by drodriguez on 2019-12-11.
 */

fun makePayment(amount: Int = 100): Response {
    return makeRequest(formatRequest("""
        mutation {
          makePayment(patientId: "23b5f391-9af3-4dbf-a291-d32f5e1c3c3f"
            practiceGuid: "54a08c18-a22f-44e1-9864-25f79995d71c"
            amount: $amount
          )
        }
    """), cognitoToken)
}