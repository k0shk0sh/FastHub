package com.fastaccess.domain.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Kosh on 11.05.18.
 */
data class AccessTokenResponse(val id: Long? = null,
                               val token: String? = null,
                               val hashedToken: String? = null,
                               val accessToken: String? = null,
                               val tokenType: String? = null)

data class AuthBodyModel(val clientId: String? = null,
                         val clientSecret: String? = null,
                         val redirectUri: String? = null,
                         val scopes: List<String>? = null,
                         val state: String? = null,
                         val note: String? = null,
                         val noteUrl: String? = null,
                         @SerializedName("X-GitHub-OTP") val otpCode: String? = null)