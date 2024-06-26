package com.elhady.instabugchallenge.utils

import android.util.Log
import com.elhady.instabugchallenge.data.HttpErrorType
import com.elhady.instabugchallenge.data.ParameterValue
import com.elhady.instabugchallenge.data.RequestType
import com.elhady.instabugchallenge.data.RequestURL
import com.elhady.instabugchallenge.data.Response
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

object NetworkCallHelper {

    fun makeApiCall(request: RequestURL): Response {
        var httpURLConnection: HttpURLConnection? = null
        var responseCode = -1
        var response = Response()

        try {
            //Adding query parameters to url
            val myUrl = addQueryToUrl(request.url, request.queryParameters)
            Log.d(TAG, "MakeNetworkCall: URL -> $myUrl")
            // open Http connection
            val url = URL(myUrl)
            httpURLConnection = url.openConnection() as HttpURLConnection
            // setting the  Request Method Type
            httpURLConnection.requestMethod = request.requestType.name
            // Enable (doOutput) in POST request only to write content to the connection output stream
            if (request.requestType == RequestType.POST) {
                httpURLConnection.doOutput = true
            }
            // Add all request headers to the Http Connection
            request.headersParameters.forEach {
                httpURLConnection.setRequestProperty(it.key, it.value)
            }
            // Add all request body to the Http Connection
            if (request.requestBody.isNotEmpty()) {
                httpURLConnection.outputStream.use { os ->
                    val input: ByteArray = request.requestBody.toByteArray()
                    os.write(input, 0, input.size)
                }
            }

            responseCode = httpURLConnection.responseCode
            Log.d(TAG, "MakeNetworkCall: responseCode -> $responseCode")

            val responseErrorType = getResponseType(responseCode)

            val responseHeaders = getResponseHeaders(httpURLConnection.headerFields)
            Log.d("MakeNetworkCall: size -> ", httpURLConnection.headerFields.size.toString())


            val finalRes = readResponseBody(httpURLConnection.inputStream)


            response = Response(
                headers = responseHeaders,
                requestURL = request,
                responseCode = responseCode,
                responseBody = finalRes,
                errorType = responseErrorType,
                errorMessage = null
            )

        } catch (e: Exception) {
            Response(
                headers = null,
                responseCode = responseCode,
                requestURL = request,
                responseBody = httpURLConnection?.responseMessage.toString(),
                errorMessage = e.toString()
            )
        } finally {
            httpURLConnection?.disconnect()
        }

        return response
    }

}


private fun addQueryToUrl(url: String, queryParameters: List<ParameterValue>): String {
    if (queryParameters.isNotEmpty()) {
        val query = StringBuilder()
        query.append("?")
        queryParameters.forEach {
            query.append(it.key + "=" + it.value)
            query.append("&")
        }
        return url + query.toString()
    }
    return url
}

private fun getResponseHeaders(headerFields: Map<String, MutableList<String>>): List<ParameterValue> {
    return headerFields.map {
        return@map ParameterValue(it.key ?: "", it.value.first())
    }.toList()
}

fun readResponseBody(inputStream: InputStream?): String {
    val input: InputStream = BufferedInputStream(inputStream)
    val reader = BufferedReader(InputStreamReader(input))
    val result = StringBuilder()
    var line: String?

    // Read the response body as string
    while (reader.readLine().also { line = it } != null) {
        result.append(line)
    }

    val finalRes = result.toString().replace("\\", "")
    return finalRes
}


private fun getResponseType(responseCode: Int): HttpErrorType {
    return when (responseCode) {
        in 200..299 -> HttpErrorType.Parsing
        400 -> HttpErrorType.BadRequest
        401 -> HttpErrorType.Unauthorized
        404 -> HttpErrorType.NotFound
        500 -> HttpErrorType.ServerError
        else -> HttpErrorType.Unknown
    }
}

private const val TAG = "MakeNetworkCall"

