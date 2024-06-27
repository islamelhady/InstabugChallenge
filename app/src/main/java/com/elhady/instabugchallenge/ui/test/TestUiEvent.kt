package com.elhady.instabugchallenge.ui.test



sealed interface TestUiEvent {
    data object SendRequestEvent : TestUiEvent
}