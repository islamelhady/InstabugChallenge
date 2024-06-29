package com.elhady.instabugchallenge.ui.test

import com.elhady.instabugchallenge.data.Response

data class TestAPIsUiState(
    val response: Response? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isUrlValid: Boolean = false,
    val isRequestTypeValid: Boolean = false,
    val errorMessage: String = "",
)