package com.elhady.instabugchallenge.data.repository

import com.elhady.instabugchallenge.data.RequestURL
import com.elhady.instabugchallenge.data.Response

interface TestAPIsRepository {
    fun testAPIsRequest(requestUrl: RequestURL): Response
}