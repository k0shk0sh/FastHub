package com.fastaccess.data.persistence.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fastaccess.data.model.CountModel
import com.fastaccess.data.model.RepoLanguageModel
import com.fastaccess.data.persistence.models.UserModel.Companion.TABLE_NAME
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = TABLE_NAME)
data class UserModel(
    @PrimaryKey @SerializedName("databaseId", alternate = ["id"]) var id: Int,
    @SerializedName("login") var login: String? = null,
    @SerializedName(value = "avatar_url", alternate = ["avatarUrl"]) var avatarUrl: String? = null,
    @SerializedName("url") var url: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("company") var company: String? = null,
    @SerializedName("blog", alternate = ["websiteUrl"]) var blog: String? = null,
    @SerializedName("location") var location: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("bio") var bio: String? = null,
    @SerializedName("created_at", alternate = ["createdAt"]) var createdAt: Date? = null,
    @SerializedName("updated_at") var updatedAt: Date? = null,
    @SerializedName("viewerCanFollow") var viewerCanFollow: Boolean? = null,
    @SerializedName("viewerIsFollowing") var viewerIsFollowing: Boolean? = null,
    @SerializedName("isViewer") var isViewer: Boolean? = null,
    @SerializedName("isDeveloperProgramMember") var isDeveloperProgramMember: Boolean? = null,
    @SerializedName("followers") var followers: CountModel? = null,
    @SerializedName("following") var following: CountModel? = null,
    @SerializedName("organizations") var organizations: UserOrganizationModel? = null,
    @SerializedName("pinnedRepositories") var pinnedRepositories: UserPinnedReposModel? = null
) {
    companion object {
        const val TABLE_NAME = "user_table"
    }
}

data class UserOrganizationModel(
    @SerializedName("totalCount") var totalCount: Int? = null,
    @SerializedName("nodes") var nodes: List<UserOrganizationNodesModel>? = null
)

data class UserOrganizationNodesModel(
    @SerializedName(value = "avatar_url", alternate = ["avatarUrl"]) var avatarUrl: String? = null,
    @SerializedName("location") var location: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("login") var login: String? = null,
    @SerializedName("name") var name: String? = null
)

data class UserPinnedReposModel(
    @SerializedName("totalCount") var totalCount: Int? = null,
    @SerializedName("nodes") var pinnedRepositories: List<UserPinnedRepoNodesModel>? = null
)

data class UserPinnedRepoNodesModel(
    @SerializedName("name") var name: String? = null,
    @SerializedName("nameWithOwner") var nameWithOwner: String? = null,
    @SerializedName("primaryLanguage") var primaryLanguage: RepoLanguageModel? = null,
    @SerializedName("stargazers") var stargazers: CountModel? = null,
    @SerializedName("issues") var issues: CountModel? = null,
    @SerializedName("pullRequests") var pullRequests: CountModel? = null,
    @SerializedName("forkCount") var forkCount: Int? = null
)