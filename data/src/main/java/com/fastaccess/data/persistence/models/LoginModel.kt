package com.fastaccess.data.persistence.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fastaccess.data.model.FragmentType
import com.fastaccess.data.persistence.models.LoginModel.Companion.TABLE_NAME
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = TABLE_NAME)
data class LoginModel(
        @PrimaryKey @SerializedName("id") var id: Long,
        @SerializedName("login") var login: String? = null,
        @SerializedName("avatar_url") var avatarUrl: String? = null,
        @SerializedName("gravatar_id") var gravatarId: String? = null,
        @SerializedName("url") var url: String? = null,
        @SerializedName("html_url") var htmlUrl: String? = null,
        @SerializedName("type") var type: String? = null,
        @SerializedName("site_admin") var siteAdmin: Boolean? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("company") var company: String? = null,
        @SerializedName("blog") var blog: String? = null,
        @SerializedName("location") var location: String? = null,
        @SerializedName("email") var email: String? = null,
        @SerializedName("hireable") var hireable: Boolean? = null,
        @SerializedName("bio") var bio: String? = null,
        @SerializedName("public_repos") var publicRepos: Int? = null,
        @SerializedName("public_gists") var publicGists: Int? = null,
        @SerializedName("followers") var followers: Int? = null,
        @SerializedName("following") var following: Int? = null,
        @SerializedName("created_at") var createdAt: Date? = null,
        @SerializedName("updated_at") var updatedAt: Date? = null,
        @SerializedName("isLoggedIn") var isLoggedIn: Boolean? = false,
        @SerializedName("isEnterprise") var isEnterprise: Boolean? = false,
        @SerializedName("token") var token: String? = null,
        @SerializedName("otpCode") var otpCode: String? = null,
        @SerializedName("enterpriseUrl") var enterpriseUrl: String? = null
) {

    fun toRepos() = "$htmlUrl?tab=${FragmentType.REPOS.tabName}"
    fun toStarred() = "$htmlUrl?tab=${FragmentType.STARRED.tabName}"
    fun toGists() = "$htmlUrl?tab=${FragmentType.GISTS.tabName}"
    fun toFollowers() = "$htmlUrl?tab=${FragmentType.FOLLOWERS.tabName}"
    fun toFollowing() = "$htmlUrl?tab=${FragmentType.FOLLOWINGS.tabName}"

    companion object {
        const val TABLE_NAME = "login_table"
    }
}