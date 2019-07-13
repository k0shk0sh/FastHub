package com.fastaccess.data.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Kosh on 27.01.19.
 */
data class EmbeddedRepoModel(
    @SerializedName("nameWithOwner") var nameWithOwner: String? = null
)