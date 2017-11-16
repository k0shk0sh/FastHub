package com.fastaccess.provider.rest.interceptors

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by Kosh on 05 Jul 2017, 8:14 PM
 */

class ContentTypeInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return chain.proceed(request.newBuilder()
                .addHeader("Accept", "application/vnd.github.v3+json")
                .addHeader("Content-type", "application/vnd.github.v3+json")
                .method(request.method(), request.body())
                .build())
    }
}