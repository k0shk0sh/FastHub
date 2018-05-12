package com.fastaccess.domain.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Kosh on 06.05.18.
 */
data class UserResponse(
        @SerializedName("id") val id: Long?,
        @SerializedName("login") val login: String?,
        @SerializedName("avatar_url") val avatarUrl: String?,
        @SerializedName("gravatar_id") val gravatarId: String?,
        @SerializedName("url") val url: String?,
        @SerializedName("html_url") val htmlUrl: String?,
        @SerializedName("type") val type: String?,
        @SerializedName("site_admin") val siteAdmin: Boolean?,
        @SerializedName("name") val name: String?,
        @SerializedName("company") val company: String?,
        @SerializedName("blog") val blog: String?,
        @SerializedName("location") val location: String?,
        @SerializedName("email") val email: String?,
        @SerializedName("hireable") val hireable: Boolean?,
        @SerializedName("bio") val bio: String?,
        @SerializedName("public_repos") val publicRepos: Int?,
        @SerializedName("public_gists") val publicGists: Int?,
        @SerializedName("followers") val followers: Int?,
        @SerializedName("following") val following: Int?,
        @SerializedName("created_at") val createdAt: String?,
        @SerializedName("updated_at") val updatedAt: String?
)