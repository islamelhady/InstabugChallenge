package com.elhady.instabugchallenge.domain

import android.util.Patterns

class CheckApiUseCase {
    operator fun invoke(url: String): Boolean {
        return url.isNotEmpty() && Patterns.WEB_URL.matcher(url).matches()
    }
}