package com.elhady.instabugchallenge

import android.app.Application
import com.elhady.instabugchallenge.data.local.CacheManager

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        CacheManager.init(this)
    }
}