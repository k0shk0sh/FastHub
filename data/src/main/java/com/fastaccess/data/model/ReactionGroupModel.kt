package com.fastaccess.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class ReactionGroupModel(
    @SerializedName("content") var content: String? = null,
    @SerializedName("createdAt") var createdAt: Date? = null,
    @SerializedName("users") var users: CountModel? = null,
    @SerializedName("viewerHasReacted") var viewerHasReacted: Boolean? = false
)