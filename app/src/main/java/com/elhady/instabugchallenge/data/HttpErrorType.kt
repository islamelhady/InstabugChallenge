package com.elhady.instabugchallenge.data

sealed class HttpErrorType {
    data object Unauthorized : HttpErrorType()
    data object BadRequest : HttpErrorType()
    data object NotFound : HttpErrorType()
    data object ServerError : HttpErrorType()
    data object Unknown : HttpErrorType()
    data object Parsing : HttpErrorType()

}