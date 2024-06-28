package com.elhady.instabugchallenge.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.elhady.instabugchallenge.data.RequestType
import com.elhady.instabugchallenge.data.StatusType

object CacheManager {
    private lateinit var databaseHelper: CacheDatabaseHelper

    fun init(context: Context) {
        databaseHelper = CacheDatabaseHelper(context)
    }

    fun addRequest(cachedRequest: CachedRequest) {
        val db = databaseHelper.writableDatabase
        val values = ContentValues().apply {
            put(CacheDatabaseHelper.COLUMN_URL, cachedRequest.url)
            put(CacheDatabaseHelper.COLUMN_CONTENT_JSON, cachedRequest.contentJson)
            put(CacheDatabaseHelper.COLUMN_CONTENT_MULTIPART, cachedRequest.contentMultipart)
            put(CacheDatabaseHelper.COLUMN_HEADERS, cachedRequest.headers)
            put(CacheDatabaseHelper.COLUMN_QUERY_PARAMS, cachedRequest.queryParams)
            put(CacheDatabaseHelper.COLUMN_RESPONSE_BODY, cachedRequest.responseBody)
            put(CacheDatabaseHelper.COLUMN_REQUEST_TYPE, cachedRequest.requestType.name)
            put(CacheDatabaseHelper.COLUMN_EXECUTION_TIME, cachedRequest.executionTime)
            put(CacheDatabaseHelper.COLUMN_RESPONSE_CODE, cachedRequest.responseCode)
        }
        db.insert(CacheDatabaseHelper.TABLE_NAME, null, values)
    }

    fun getRequests(): List<CachedRequest> {
        val db = databaseHelper.readableDatabase
        val cursor = db.query(
            CacheDatabaseHelper.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )
        return cursorToList(cursor)
    }

    fun getRequestsSortedByExecutionTime(): List<CachedRequest> {
        val db = databaseHelper.readableDatabase
        val cursor = db.query(
            CacheDatabaseHelper.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "${CacheDatabaseHelper.COLUMN_EXECUTION_TIME} ASC"
        )
        return cursorToList(cursor)
    }

    fun getRequestsFilteredByType(type: RequestType): List<CachedRequest> {
        val db = databaseHelper.readableDatabase
        val cursor = db.query(
            CacheDatabaseHelper.TABLE_NAME,
            null,
            "${CacheDatabaseHelper.COLUMN_REQUEST_TYPE} = ?", arrayOf(type.name),
            null,
            null,
            null
        )
        return cursorToList(cursor)
    }

    fun getRequestsFilteredByStatus(status: StatusType): List<CachedRequest> {
        val db = databaseHelper.readableDatabase
        val successCodes = arrayOf("200")
        val failureCodes = arrayOf("400", "401", "403", "404", "500")

        val selection = when (status) {
            StatusType.SUCCESS -> "${CacheDatabaseHelper.COLUMN_RESPONSE_CODE} IN (${successCodes.joinToString(",")})"
            StatusType.FAILURE -> "${CacheDatabaseHelper.COLUMN_RESPONSE_CODE} IN (${failureCodes.joinToString(",")})"
        }

        val cursor = db.query(
            CacheDatabaseHelper.TABLE_NAME,
            null,
            selection,
            null,
            null,
            null,
            null
        )
        return cursorToList(cursor)
    }


    fun clearAllCached() {
        val db = databaseHelper.writableDatabase
        db.delete(CacheDatabaseHelper.TABLE_NAME, null, null)
        db.close()
    }


    private fun cursorToList(cursor: Cursor): List<CachedRequest> {
        val requests = mutableListOf<CachedRequest>()
        while (cursor.moveToNext()) {
            val url = cursor.getString(cursor.getColumnIndexOrThrow(CacheDatabaseHelper.COLUMN_URL))
            val contentJson =
                cursor.getString(cursor.getColumnIndexOrThrow(CacheDatabaseHelper.COLUMN_CONTENT_JSON))
            val contentMultipart =
                cursor.getString(cursor.getColumnIndexOrThrow(CacheDatabaseHelper.COLUMN_CONTENT_MULTIPART))
            val headers =
                cursor.getString(cursor.getColumnIndexOrThrow(CacheDatabaseHelper.COLUMN_HEADERS))
            val queryParams =
                cursor.getString(cursor.getColumnIndexOrThrow(CacheDatabaseHelper.COLUMN_QUERY_PARAMS))
            val responseBody =
                cursor.getString(cursor.getColumnIndexOrThrow(CacheDatabaseHelper.COLUMN_RESPONSE_BODY))
            val executionTime =
                cursor.getLong(cursor.getColumnIndexOrThrow(CacheDatabaseHelper.COLUMN_EXECUTION_TIME))
            val responseCode =
                cursor.getInt(cursor.getColumnIndexOrThrow(CacheDatabaseHelper.COLUMN_RESPONSE_CODE))
            val requestTypeString =
                cursor.getString(cursor.getColumnIndexOrThrow(CacheDatabaseHelper.COLUMN_REQUEST_TYPE))
            val requestType = RequestType.valueOf(requestTypeString)

            requests.add(
                CachedRequest(
                    url = url,
                    contentJson = contentJson,
                    contentMultipart = contentMultipart,
                    headers = headers,
                    queryParams = queryParams,
                    responseBody = responseBody,
                    executionTime = executionTime,
                    responseCode = responseCode,
                    requestType = requestType
                )
            )
        }
        cursor.close()
        return requests
    }
}
