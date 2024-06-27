package com.elhady.instabugchallenge.ui.test

import com.elhady.instabugchallenge.data.ParameterValue
import com.elhady.instabugchallenge.data.RequestType
import com.elhady.instabugchallenge.data.Response

data class TestAPIsUiState(
//    val url: String = "",
//    val requestType: RequestType = RequestType.GET,
//    val headers: List<ParameterValue>? = emptyList(),
//    val queryParameters: List<ParameterValue>? = emptyList(),
//    val requestBody: String = "",
    val response: Response? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isUrlValid: Boolean = false,
    val isRequestTypeValid: Boolean = false,
    val onErrors: List<String> = emptyList(),
    val errorMessage: String = ""
)
