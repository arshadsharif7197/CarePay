package com.carecloud.carepaylibray.androidTest.providers
import com.google.gson.Gson
import okhttp3.*
import java.lang.Exception
import com.carecloud.carepaylibray.androidTest.graphqldatamodels.Response
import com.carecloud.carepaylibray.androidTest.graphqldatamodels.SessionTokens
import com.carecloud.carepaylibray.androidTest.graphqlrequests.getBreezeToken

var tokens: SessionTokens? = null
var cognitoToken: String? = null
var xavierToken: String? = null

fun initXavierProvider(appMode: String = "practice") {
    if (tokens === null) {
        val response = makeRequest(getBreezeToken(appMode = appMode))
        cognitoToken = response.data?.getBreezeSessionToken?.cognito_token?.authenticationToken
        xavierToken = response.data?.getBreezeSessionToken?.xavier_token
    }
}

fun makeRequest(body: String, authHeader: String? = ""): Response {
    val graphQLUrl = "https://xavier.qa.carecloud.com/"
    val json = MediaType.parse("application/json")
    val jsonBody = RequestBody.create(json, body)

    val client = OkHttpClient()
    var builder = Request.Builder()
                .url(graphQLUrl)
                .post(jsonBody)
    if (!authHeader.isNullOrEmpty()) {
        builder = builder.addHeader("Authorization", authHeader)
    }
    val request = builder.build()
    var response: okhttp3.Response?
    try {
        response = client.newCall(request).execute()
    } catch (e: Exception) {
        println("Error making the request: $e")
        throw e
    }
    val gson = Gson()
    return gson.fromJson(response?.body()?.string(), Response::class.java)
}

fun formatRequest(query: String, replace: Boolean = true): String {
    val cleanString = query.trimIndent().replace("\n", "")
    val formattedQuery = if (replace) cleanString.replace("\"", "\\\"") else cleanString
    return """{"query":" $formattedQuery "}"""
}
