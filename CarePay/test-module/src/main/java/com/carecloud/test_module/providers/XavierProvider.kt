package com.carecloud.test_module.providers
import com.google.gson.Gson
import okhttp3.*
import java.lang.Exception
import com.carecloud.test_module.graphqldatamodels.Response


fun makeRequest(body: String, authHeader: String = ""): Response {

    val json = MediaType.parse("application/json")
    val graphQLUrl = "https://xavier.qa.carecloud.com/"
    val jsonBody = RequestBody.create(json, body)

    val client = OkHttpClient()
    var builder = Request.Builder()
            .url(graphQLUrl)
            .post(jsonBody)
    if (!authHeader.isNullOrEmpty()) {
        builder = builder.addHeader("Authorization", authHeader)
    }
    val request = builder.build()
    var response: okhttp3.Response? = null
    try {
        response = client.newCall(request).execute()
    } catch (e: Exception) {
        println("Error making the request: $e")
        throw e
    }
    val gson = Gson()
    return gson.fromJson(response?.body()?.string(), Response::class.java)
}