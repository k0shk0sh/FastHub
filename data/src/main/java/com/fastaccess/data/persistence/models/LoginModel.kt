package com.fastaccess.data.persistence.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fastaccess.data.persistence.models.LoginModel.Companion.TABLE_NAME
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = TABLE_NAME)
data class LoginModel(
        @PrimaryKey @SerializedName("id") val id: Long,
        @SerializedName("login") val login: String? = null,
        @SerializedName("avatar_url") val avatarUrl: String? = null,
        @SerializedName("gravatar_id") val gravatarId: String? = null,
        @SerializedName("url") val url: String? = null,
        @SerializedName("html_url") val htmlUrl: String? = null,
        @SerializedName("type") val type: String? = null,
        @SerializedName("site_admin") val siteAdmin: Boolean? = null,
        @SerializedName("name") val name: String? = null,
        @SerializedName("company") val company: String? = null,
        @SerializedName("blog") val blog: String? = null,
        @SerializedName("location") val location: String? = null,
        @SerializedName("email") val email: String? = null,
        @SerializedName("hireable") val hireable: Boolean? = null,
        @SerializedName("bio") val bio: String? = null,
        @SerializedName("public_repos") val publicRepos: Int? = null,
        @SerializedName("public_gists") val publicGists: Int? = null,
        @SerializedName("followers") val followers: Int? = null,
        @SerializedName("following") val following: Int? = null,
        @SerializedName("created_at") val createdAt: Date? = null,
        @SerializedName("updated_at") val updatedAt: Date? = null,
        @SerializedName("isLoggedIn") val isLoggedIn: Boolean? = false,
        @SerializedName("isEnterprise") val isEnterprise: Boolean? = false,
        @SerializedName("otpCode") val otpCode: String? = null,
        @SerializedName("enterpriseUrl") val enterpriseUrl: String? = null
) {
    companion object {
        const val TABLE_NAME = "login_table"
    }
}