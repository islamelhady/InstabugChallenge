package com.elhady.instabugchallenge.data.repository

import com.elhady.instabugchallenge.data.RequestURL
import com.elhady.instabugchallenge.data.Response
import com.elhady.instabugchallenge.utils.NetworkCallHelper.makeApiCall

class TestAPIsRepositoryImp: TestAPIsRepository {
    override fun testAPIsRequest(requestUrl: RequestURL): Response {
        return makeApiCall(request = requestUrl)
    }
}