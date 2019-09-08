package com.fastaccess.domain.response

import com.google.gson.annotations.SerializedName

data class GithubErrorResponse(
    @SerializedName("message") var message: String? = null,
    @SerializedName("documentation_url") var documentationUrl: String? = null,
    @SerializedName("errors") var errors: List<String?>? = null
)