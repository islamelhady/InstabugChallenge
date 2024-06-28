package com.elhady.instabugchallenge.data.repository

import android.util.Log
import com.elhady.instabugchallenge.data.RequestType
import com.elhady.instabugchallenge.data.RequestURL
import com.elhady.instabugchallenge.data.Response
import com.elhady.instabugchallenge.data.StatusType
import com.elhady.instabugchallenge.data.local.CacheManager
import com.elhady.instabugchallenge.data.local.CachedRequest
import com.elhady.instabugchallenge.domain.TestAPIsRepository
import com.elhady.instabugchallenge.utils.NetworkCallHelper

class TestAPIsRepositoryImp(
    private val networkCallHelper: NetworkCallHelper,
    private val cacheManager: CacheManager
) : TestAPIsRepository {

    override fun testAPIsRequest(requestUrl: RequestURL): Response {
        val response = networkCallHelper.makeApiCall(request = requestUrl)
        cacheAPIsRequest(response)
        return response
    }

    override fun cacheAPIsRequest(response: Response) {
        response.requestURL?.let { requestURL ->
            val cachedRequest = CachedRequest(
                url = requestURL.url,
                contentJson = requestURL.contentJson,
                contentMultipart = requestURL.contentMultipart,
                headers = requestURL.headersParameters.joinToString("\n") { it.key + ": " + it.value },
                queryParams = requestURL.queryParameters.joinToString("\n") { it.key + ": " + it.value },
                responseBody = response.responseBody,
                executionTime = response.executionTime,
                responseCode = response.responseCode,
                requestType = requestURL.requestType
            )
            cacheManager.addRequest(cachedRequest)
            Log.d("TestAPIsRepositoryImp", "Request cached: $cachedRequest")
        }
    }

    override fun getRequests(): List<CachedRequest> {
        return cacheManager.getRequests()
    }

    override fun getRequestsSortedByExecutionTime(): List<CachedRequest> {
        return cacheManager.getRequestsSortedByExecutionTime()
    }

    override fun getCachedRequestsFilteredByType(type: RequestType): List<CachedRequest> {
        return cacheManager.getRequestsFilteredByType(type)
    }

    override fun getCachedRequestsFilterByStatusType(status: StatusType): List<CachedRequest> {
        return cacheManager.getRequestsFilteredByStatus(status)
    }

    override fun clearCache() {
        cacheManager.clearAllCached()
    }

}