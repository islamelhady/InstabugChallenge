package com.elhady.instabugchallenge.data


data class RequestURL(
    val url: String = "",
    val requestType: RequestType = RequestType.GET,
    val headersParameters: List<ParameterValue> = emptyList(),
    val queryParameters: List<ParameterValue> = emptyList(),
    val contentJson: String? = "",
    val contentMultipart: String? = "",
)

enum class RequestType {
    GET,
    POST
}

enum class StatusType {
    SUCCESS, FAILURE
}