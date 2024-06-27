package com.elhady.instabugchallenge.data

import com.elhady.instabugchallenge.utils.NetworkCallHelper.makeApiCall

class TestAPIsRepository {
    fun testAPIsRequest(requestUrl: RequestURL) = makeApiCall(request = requestUrl)

}