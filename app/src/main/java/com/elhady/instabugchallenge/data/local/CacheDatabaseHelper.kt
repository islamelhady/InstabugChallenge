package com.elhady.instabugchallenge.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CacheDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME,null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_URL TEXT,
                $COLUMN_CONTENT_JSON TEXT,
                $COLUMN_CONTENT_MULTIPART TEXT,
                $COLUMN_HEADERS TEXT,
                $COLUMN_QUERY_PARAMS TEXT,
                $COLUMN_RESPONSE_BODY TEXT,
                $COLUMN_REQUEST_TYPE NOT NULL,
                $COLUMN_EXECUTION_TIME INTEGER,
                $COLUMN_RESPONSE_CODE INTEGER
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "cache.db"
        const val DATABASE_VERSION = 4
        const val TABLE_NAME = "cached_requests"
        const val COLUMN_ID = "id"
        const val COLUMN_URL = "url"
        const val COLUMN_CONTENT_JSON = "request_body_json"
        const val COLUMN_CONTENT_MULTIPART = "request_body_multipart"
        const val COLUMN_HEADERS = "headers"
        const val COLUMN_QUERY_PARAMS = "query_params"
        const val COLUMN_RESPONSE_BODY = "response_body"
        const val COLUMN_REQUEST_TYPE = "request_type"
        const val COLUMN_EXECUTION_TIME = "execution_time"
        const val COLUMN_RESPONSE_CODE = "response_code"
    }

}
