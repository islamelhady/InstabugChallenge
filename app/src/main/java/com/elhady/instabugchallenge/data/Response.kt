package com.elhady.instabugchallenge.data

data class Response(
    var responseCode: Int = 0,
    var errorCode: String = "",
    var requestURL: RequestURL? = RequestURL(),
    var responseBody: String = "",
    var headers: List<ParameterValue>? = emptyList(),
    var executionTime: Long = 0
)
