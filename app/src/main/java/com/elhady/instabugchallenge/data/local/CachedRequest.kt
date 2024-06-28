package com.elhady.instabugchallenge.data.local

import com.elhady.instabugchallenge.data.RequestType

data class CachedRequest(
    val url: String,
    val contentJson: String?,
    val contentMultipart: String?,
    val headers: String,
    val queryParams: String,
    val requestType: RequestType,
    val responseBody: String,
    val executionTime: Long,
    val responseCode: Int
)

