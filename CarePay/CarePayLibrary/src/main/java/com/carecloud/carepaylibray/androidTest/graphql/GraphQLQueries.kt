package com.carecloud.carepaylibray.androidTest.graphql

/**
 * Created by drodriguez on 2019-10-15.
 */
fun getBreezeToken(username: String = "automation+breezetest@carecloud.com", password: String = "Test123!", appMode: String): String {
    return createRequest("""
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
    return createRequest("""
        { getUserToken(
            username: "$username",
            password: "$password") 
        }""")
}