package com.fastaccess.data.persistence.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.fastaccess.data.model.CountModel
import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.model.RepoLanguageModel
import com.google.gson.annotations.SerializedName
import github.GetProfileStarredReposQuery
import java.util.*

/**
 * Created by Kosh on 08.10.18.
 */
data class ProfileStarredReposModel(
    @SerializedName("totalCount") var totalCount: Int = 0,
    @SerializedName("repos") var repos: List<ProfileStarredRepoModel>? = null,
    @Ignore @SerializedName("pageInfo") var pageInfo: PageInfoModel? = null
) {
    companion object {
        fun newInstance(response: GetProfileStarredReposQuery.Data?, login: String): ProfileStarredReposModel? {
            return response?.user?.starredRepositories?.let { repos ->
                ProfileStarredReposModel(repos.totalCount, ProfileStarredRepoModel.newInstances(repos.nodes, login),
                    PageInfoModel(repos.pageInfo.startCursor, repos.pageInfo.endCursor,
                        repos.pageInfo.isHasNextPage, repos.pageInfo.isHasPreviousPage))
            }
        }
    }
}

@Entity(tableName = ProfileStarredRepoModel.TABLE_NAME)
data class ProfileStarredRepoModel(
    @PrimaryKey @SerializedName("id") var id: String = "",
    @SerializedName("databaseId") var databaseId: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("nameWithOwner") var nameWithOwner: String? = null,
    @SerializedName("updatedAt") var updatedAt: Date? = null,
    @SerializedName("diskUsage") var diskUsage: Int? = null,
    @SerializedName("primaryLanguage") @Embedded(prefix = "language_") var primaryLanguage: RepoLanguageModel? = null,
    @SerializedName("stargazers") @Embedded(prefix = "stargazers_") var stargazers: CountModel? = null,
    @SerializedName("issues") @Embedded(prefix = "issues_") var issues: CountModel? = null,
    @SerializedName("pullRequests") @Embedded(prefix = "pullRequests_") var pullRequests: CountModel? = null,
    @SerializedName("forkCount") var forkCount: Int? = null,
    @SerializedName("isFork") var isFork: Boolean? = null,
    @SerializedName("isPrivate") var isPrivate: Boolean? = null,
    @SerializedName("viewerHasStarred") var viewerHasStarred: Boolean? = null,
    @SerializedName("login") var login: String? = null
) {
    companion object {
        const val TABLE_NAME = "profile_starred_repo_table"

        internal fun newInstances(list: List<GetProfileStarredReposQuery.Node>?, login: String): List<ProfileStarredRepoModel> {
            return list?.asSequence()?.map { data ->
                ProfileStarredRepoModel(data.id, data.databaseId, data.name, data.nameWithOwner, data.updatedAt, data.diskUsage,
                    RepoLanguageModel(data.primaryLanguage?.name, data.primaryLanguage?.color),
                    CountModel(data.stargazers.totalCount), CountModel(data.issues.totalCount),
                    CountModel(data.pullRequests.totalCount), data.forkCount, data.isFork,
                    data.isPrivate, data.isViewerHasStarred, login)
            }?.toList() ?: arrayListOf()
        }
    }
}