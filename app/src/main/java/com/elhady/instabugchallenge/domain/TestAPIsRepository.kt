package com.elhady.instabugchallenge.domain

import com.elhady.instabugchallenge.data.RequestType
import com.elhady.instabugchallenge.data.RequestURL
import com.elhady.instabugchallenge.data.Response
import com.elhady.instabugchallenge.data.StatusType
import com.elhady.instabugchallenge.data.local.CachedRequest

interface TestAPIsRepository {
    fun testAPIsRequest(requestUrl: RequestURL): Response
    fun cacheAPIsRequest(response: Response)
    fun getRequests(): List<CachedRequest>
    fun getRequestsSortedByExecutionTime(): List<CachedRequest>
    fun getCachedRequestsFilteredByType(type: RequestType): List<CachedRequest>
    fun getCachedRequestsFilterByStatusType(status: StatusType): List<CachedRequest>
    fun clearCache()
}