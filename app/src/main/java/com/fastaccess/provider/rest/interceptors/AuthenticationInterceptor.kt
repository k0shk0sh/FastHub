package com.fastaccess.provider.rest.interceptors

import com.fastaccess.helper.Logger
import com.fastaccess.helper.PrefGetter
import com.fastaccess.provider.scheme.LinkParserHelper
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthenticationInterceptor : Interceptor {
    private var isScrapping: Boolean = false
    private var token: String? = null
    private var otp: String? = null

    constructor(token: String? = null, otp: String? = null) {
        this.token = token
        this.otp = otp
    }

    @JvmOverloads constructor(isScrapping: Boolean = false) {
        this.isScrapping = isScrapping
    }

    @Throws(IOException::class) override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()
        val isEnterprise = LinkParserHelper.isEnterprise(original.url().host())
        val authToken = if (token.isNullOrBlank()) if (isEnterprise) PrefGetter.getEnterpriseToken() else PrefGetter.getToken() else token
        val otpCode = if (otp.isNullOrBlank()) if (isEnterprise) PrefGetter.getEnterpriseOtpCode() else PrefGetter.getOtpCode() else otp
        if (!authToken.isNullOrBlank()) {
            builder.header("Authorization", if (authToken!!.startsWith("Basic")) authToken else "token " + authToken)
        }
        if (!otpCode.isNullOrBlank()) {
            builder.addHeader("X-GitHub-OTP", otpCode!!.trim())
        }
        if (!isScrapping) builder.addHeader("User-Agent", "FastHub")
        val request = builder.build()
        return chain.proceed(request)
    }
}