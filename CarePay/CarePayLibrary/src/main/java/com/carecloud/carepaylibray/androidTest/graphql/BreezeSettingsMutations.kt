package com.carecloud.carepaylibray.androidTest.graphql

/**
 * Created by drodriguez on 2019-10-24.
 */

fun changePaymentSetting(location: String): String {
    return createRequest("""
        mutation {
          changePaymentSettings(input: { setWorkflowPlacement: "$location" }
            practiceId: "54a08c18-a22f-44e1-9864-25f79995d71c"
          )
        }
    """)
}