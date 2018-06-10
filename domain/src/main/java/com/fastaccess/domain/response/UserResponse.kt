package com.fastaccess.domain.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Kosh on 06.05.18.
 */
data class UserResponse(
        @SerializedName("id") var id: Long?,
        @SerializedName("login") var login: String?,
        @SerializedName("avatar_url") var avatarUrl: String?,
        @SerializedName("gravatar_id") var gravatarId: String?,
        @SerializedName("url") var url: String?,
        @SerializedName("html_url") var htmlUrl: String?,
        @SerializedName("type") var type: String?,
        @SerializedName("site_admin") var siteAdmin: Boolean?,
        @SerializedName("name") var name: String?,
        @SerializedName("company") var company: String?,
        @SerializedName("blog") var blog: String?,
        @SerializedName("location") var location: String?,
        @SerializedName("email") var email: String?,
        @SerializedName("hireable") var hireable: Boolean?,
        @SerializedName("bio") var bio: String?,
        @SerializedName("public_repos") var publicRepos: Int?,
        @SerializedName("public_gists") var publicGists: Int?,
        @SerializedName("followers") var followers: Int?,
        @SerializedName("following") var following: Int?,
        @SerializedName("created_at") var createdAt: String?,
        @SerializedName("updated_at") var updatedAt: String?
)