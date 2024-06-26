package com.elhady.instabugchallenge.data


data class RequestURL(
    val url: String = "",
    val requestType: RequestType = RequestType.NONE,
    var headersParameters: List<ParameterValue> = emptyList(),
    var queryParameters: List<ParameterValue> = emptyList(),
    val requestBody: String = "",
)

enum class RequestType {
    GET,
    POST,
    NONE
}
