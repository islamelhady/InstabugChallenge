package com.elhady.instabugchallenge.ui

import com.elhady.instabugchallenge.data.RequestURL


sealed interface TestUiEvent {
    data class SendRequestEvent(val requestModel: RequestURL) : TestUiEvent
}