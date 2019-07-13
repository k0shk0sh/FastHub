package com.fastaccess.data.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Kosh on 21.01.19.
 */
data class ShortRepoModel(
    @SerializedName("id") var id: String = "",
    @SerializedName("databaseId") var databaseId: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("stargazers") var stargazers: CountModel? = null,
    @SerializedName("issues") var issues: CountModel? = null,
    @SerializedName("pullRequests") var pullRequests: CountModel? = null,
    @SerializedName("forkCount") var forkCount: Int? = null,
    @SerializedName("isFork") var isFork: Boolean? = null,
    @SerializedName("isPrivate") var isPrivate: Boolean? = null
)