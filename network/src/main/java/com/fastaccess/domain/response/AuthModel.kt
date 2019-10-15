package com.fastaccess.domain.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Kosh on 11.05.18.
 */
data class AccessTokenResponse(var id: Long? = null,
                               var token: String? = null,
                               var hashedToken: String? = null,
                               var accessToken: String? = null,
                               var tokenType: String? = null)

data class AuthBodyModel(var clientId: String? = null,
                         var clientSecret: String? = null,
                         var redirectUri: String? = null,
                         var scopes: List<String>? = null,
                         var state: String? = null,
                         var note: String? = null,
                         var noteUrl: String? = null,
                         @SerializedName("X-GitHub-OTP") var otpCode: String? = null)