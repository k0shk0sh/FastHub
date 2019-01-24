package com.fastaccess.data.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Kosh on 24.01.19.
 */
data class ShortUserModel(
    @SerializedName("id") var id: String? = null,
    @SerializedName("login") var login: String? = null,
    @SerializedName("url") var url: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("location") var location: String? = null,
    @SerializedName("bio") var bio: String? = null,
    @SerializedName(value = "avatar_url", alternate = ["avatarUrl"]) var avatarUrl: String? = null,
    @SerializedName("viewerCanFollow") var viewerCanFollow: Boolean? = null,
    @SerializedName("viewerIsFollowing") var viewerIsFollowing: Boolean? = null
)