package com.elhady.instabugchallenge.ui.cach

import com.elhady.instabugchallenge.data.local.CachedRequest

data class CacheUiState(
    val cacheResponse: List<CachedRequest> = emptyList(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String = ""
)