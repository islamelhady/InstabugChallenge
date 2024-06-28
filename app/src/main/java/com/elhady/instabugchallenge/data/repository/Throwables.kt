package com.elhady.instabugchallenge.data.repository

open class ApiThrowable(message: String?): Throwable(message)
class UnknownException: ApiThrowable("UnknownHostException")
class TimeoutThrowable: ApiThrowable("Timeout Error")

