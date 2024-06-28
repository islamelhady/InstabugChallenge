package com.elhady.instabugchallenge.utils

import android.util.Log
import com.elhady.instabugchallenge.data.ParameterValue
import com.elhady.instabugchallenge.data.RequestType
import com.elhady.instabugchallenge.data.RequestURL
import com.elhady.instabugchallenge.data.Response
import com.elhady.instabugchallenge.data.repository.ApiThrowable
import com.elhady.instabugchallenge.data.repository.TimeoutThrowable
import com.elhady.instabugchallenge.data.repository.UnknownException
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException

object NetworkCallHelper {

    private lateinit var httpURLConnection: HttpURLConnection
    private val response = Response()
    fun makeApiCall(request: RequestURL): Response {
        val startTime = System.currentTimeMillis()

        try {
            val myUrl = addQueryToUrl(request.url, request.queryParameters)
            val url = URL(myUrl)
            Log.d(TAG, "URL -> $myUrl")
            Log.d(TAG, "requestType -> ${request.requestType.name}")
            Log.d(TAG, "queryParameters -> ${request.queryParameters}")
            Log.d(TAG, "contentJson -> ${request.contentJson}")
            Log.d(TAG, "contentMultipart -> ${request.contentMultipart}")
            Log.d(TAG, "headersParameters -> ${request.headersParameters}")

            response.requestURL = request
            when (request.requestType) {
                RequestType.GET -> {
                    httpURLConnection = url.openConnection() as HttpURLConnection
                    httpURLConnection.requestMethod = request.requestType.name

                }
                RequestType.POST -> {
                    httpURLConnection = url.openConnection() as HttpURLConnection
                    httpURLConnection.requestMethod = request.requestType.name
                    httpURLConnection.readTimeout = 30000
                    httpURLConnection.connectTimeout = 60000
                    httpURLConnection.doOutput = true
                }
            }

            // Add all request headers to the Http Connection
            request.headersParameters.forEach {
                httpURLConnection.setRequestProperty(it.key, it.value)
            }
            // Add all request body to the Http Connection
            if (request.contentMultipart?.isNotEmpty() == true) {
                    val boundary = "----WebKitFormBoundary" + System.currentTimeMillis()
                    httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
                    val outputStream = httpURLConnection.outputStream
                    val writer = BufferedWriter(OutputStreamWriter(outputStream, "UTF-8"))

                    // Write file content
                    val file = File(request.contentMultipart)
                    writer.write("--$boundary\r\n")
                    writer.write("Content-Disposition: form-data; name=\"file\"; filename=\"${file.name}\"\r\n")
                    writer.write("Content-Type: ${HttpURLConnection.guessContentTypeFromName(file.name)}\r\n")
                    writer.write("\r\n")
                    writer.flush()

                    val inputStream = FileInputStream(file)
                    val buffer = ByteArray(4096)
                    var bytesRead = inputStream.read(buffer)
                    while (bytesRead != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                        bytesRead = inputStream.read(buffer)
                    }
                    outputStream.flush()
                    inputStream.close()

                    writer.write("\r\n")
                    writer.write("--$boundary--\r\n")
                    writer.flush()
                    writer.close()
            } else if (request.contentJson?.isNotEmpty() == true){
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                httpURLConnection.outputStream.use { os ->
                    val input: ByteArray = request.contentJson.toByteArray()
                    os.write(input, 0, input.size)
                }
            }

            val responseCode = httpURLConnection.responseCode
            response.errorCode = getResponseType(responseCode, request.requestType)
            Log.d(TAG, "errorCode -> ${response.errorCode} ${request.requestType.name}")
            response.responseCode = responseCode
            Log.d(TAG, "responseCode -> $responseCode")

            val responseHeaders = getResponseHeaders(httpURLConnection.headerFields)
            response.headers = responseHeaders
            Log.d(TAG, "headerFields ->$responseHeaders")

            val inputStream = if (responseCode in 200..299) {
                httpURLConnection.inputStream
            } else {
                httpURLConnection.errorStream
            }
            val finalRes = readResponseBody(inputStream)
            response.responseBody = finalRes
            Log.d(TAG, "finalRes -> $finalRes")

            response.executionTime = System.currentTimeMillis() - startTime
            Log.d(TAG, "executionTime -> ${response.executionTime}")

        } catch (e: UnknownHostException) {
            throw UnknownException()
        } catch (e: SocketTimeoutException) {
            throw TimeoutThrowable()
        } catch (e: Exception) {
            throw ApiThrowable(e.message)
        }
        finally {
            httpURLConnection.disconnect()
            Log.d(TAG, "finally")
        }

        return response
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

    private fun getResponseType(responseCode: Int, type: RequestType): String {
        return when (responseCode) {
            in 200..299 -> "Successful ${type.name}"
            400 -> "Failed ${type.name} request with $responseCode error code"
            401 -> "Failed ${type.name} request with $responseCode error code"
            404 -> "Failed ${type.name} request with $responseCode error code"
            500 -> "Failed ${type.name} request with $responseCode error code"
            else -> "Unknown"
        }
    }

    private fun getResponseHeaders(headerFields: Map<String, MutableList<String>>): List<ParameterValue> {
        return headerFields.map {
            return@map ParameterValue(it.key ?: "", it.value.first())
        }.toList()
    }

    private fun readResponseBody(inputStream: InputStream?): String {
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
}
private const val TAG = "MakeNetworkCall"

