package com.elhady.instabugchallenge.ui.test

import com.elhady.instabugchallenge.data.ParameterValue
import com.elhady.instabugchallenge.data.RequestType

sealed interface TestingIntent {
    data class SendRequest(
        val url: String,
        val requestType: RequestType,
        val headers: List<ParameterValue>,
        val queryParams: List<ParameterValue>,
        val contentJson: String?,
        val contentMultipart: String?
    ): TestingIntent
}