package com.carecloud.carepaylibray.androidTest.graphqlrequests

import com.carecloud.carepaylibray.androidTest.providers.formatRequest

/**
 * Created by drodriguez on 2019-10-15.
 */
fun getBreezeToken(appMode: String): String {
    val username = if (appMode === "patient") "dev_emails+qa.androidbreeze2@carecloud.com" else "automation+breezetest@carecloud.com"
    val password = "Test123!"
    return formatRequest("""
        { getBreezeSessionToken(
            username: "$username",
            password: "$password",
            mode: "$appMode") 
            {
             cognito_token
             xavier_token
             } 
        }""")
}

fun getUserToken(username: String = "automationp@carecloud.com", password: String = "@utomationT3am"): String {
    return formatRequest("""
        { getUserToken(
            username: "$username",
            password: "$password") 
        }""")
}