package com.fastaccess.domain.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Kosh on 11.05.18.
 */
data class AccessTokenResponse(private val id: Long? = null,
                               private val token: String? = null,
                               private val hashedToken: String? = null,
                               private val accessToken: String? = null,
                               private val tokenType: String? = null)

data class AuthBodyModel(private val clientId: String? = null,
                         private val clientSecret: String? = null,
                         private val redirectUri: String? = null,
                         private val scopes: List<String>? = null,
                         private val state: String? = null,
                         private val note: String? = null,
                         private val noteUrl: String? = null,
                         @SerializedName("X-GitHub-OTP") private val otpCode: String? = null)