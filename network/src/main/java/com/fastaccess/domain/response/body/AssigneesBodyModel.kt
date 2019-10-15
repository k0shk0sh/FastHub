package com.fastaccess.domain.response.body

import com.google.gson.annotations.SerializedName

/**
 * Created by Kosh on 29.03.19.
 */
data class AssigneesBodyModel(
    @SerializedName("assignees") var assignees: List<String>? = null,
    @SerializedName("reviewers") var reviewers: List<String>? = null
)