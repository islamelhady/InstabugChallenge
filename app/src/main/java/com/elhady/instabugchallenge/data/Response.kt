package com.elhady.instabugchallenge.data

data class Response(
    val requestURL: RequestURL? = null,
    val responseBody: String = "",
    val headers: List<ParameterValue>? = emptyList(),
    val responseCode: Int = -1,
    val errorType: HttpErrorType? = null,
    val errorMessage: String? = null,
)
