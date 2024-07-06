package com.elhady.instabugchallenge.ui.cach

import com.elhady.instabugchallenge.data.RequestType
import com.elhady.instabugchallenge.data.StatusType

sealed class CacheIntent {
    data object GetCachedRequests : CacheIntent()
    data object GetCachedRequestsSortedByExecutionTime : CacheIntent()
    data class GetCachedRequestsFilteredByType(val type: RequestType) : CacheIntent()
    data class GetCachedRequestsFilteredByStatus(val status: StatusType) : CacheIntent()
    data object ClearCache : CacheIntent()
}