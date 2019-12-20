package com.carecloud.test_module.graphql

/**
 * Created by drodriguez on 2019-10-15.
 */

fun createRequest(query: String, replace: Boolean = true): String {
    val cleanString = query.trimIndent().replace("\n", "")
    val formattedQuery = if (replace) cleanString.replace("\"", "\\\"") else cleanString
    return """{"query":" $formattedQuery "}"""
}
