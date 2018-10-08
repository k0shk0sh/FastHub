package com.fastaccess.data.persistence.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.fastaccess.data.model.CountModel
import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.model.RepoLanguageModel
import com.google.gson.annotations.SerializedName
import github.GetProfileReposQuery
import java.util.*

/**
 * Created by Kosh on 08.10.18.
 */
data class ProfileReposModel(
        @SerializedName("totalCount") var totalCount: Long = 0,
        @SerializedName("totalDiskUsage") var totalDiskUsage: Long = 0,
        @SerializedName("repos") var repos: List<ProfileRepoModel>? = null,
        @Ignore @SerializedName("pageInfo") var pageInfo: PageInfoModel? = null
) {
    companion object {
        fun newInstance(response: GetProfileReposQuery.Data): ProfileReposModel? {
            return response.user?.repositories?.let { repos ->
                ProfileReposModel(repos.totalCount, repos.totalDiskUsage, ProfileRepoModel.newInstances(repos.nodes),
                        PageInfoModel(repos.pageInfo.startCursor, repos.pageInfo.endCursor,
                                repos.pageInfo.isHasNextPage, repos.pageInfo.isHasPreviousPage))
            }
        }
    }
}

@Entity(tableName = ProfileRepoModel.TABLE_NAME)
data class ProfileRepoModel(
        @PrimaryKey @SerializedName("id") var id: String = "",
        @PrimaryKey @SerializedName("databaseId") var databaseId: Long? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("updatedAt") var updatedAt: Date? = null,
        @SerializedName("diskUsage") var diskUsage: Long?,
        @SerializedName("primaryLanguage") var primaryLanguage: RepoLanguageModel? = null,
        @SerializedName("stargazers") var stargazers: CountModel? = null,
        @SerializedName("issues") var issues: CountModel? = null,
        @SerializedName("pullRequests") var pullRequests: CountModel? = null,
        @SerializedName("forkCount") var forkCount: Long? = null
) {
    companion object {
        const val TABLE_NAME = "profile_repo_table"

        internal fun newInstances(list: List<GetProfileReposQuery.Node>?): List<ProfileRepoModel> {
            return list?.asSequence()?.map { data ->
                ProfileRepoModel(data.id, data.databaseId, data.name, data.updatedAt, data.diskUsage,
                        RepoLanguageModel(data.primaryLanguage?.name, data.primaryLanguage?.color),
                        CountModel(data.stargazers.totalCount), CountModel(data.issues.totalCount),
                        CountModel(data.pullRequests.totalCount), data.forkCount)
            }?.toList() ?: arrayListOf()
        }
    }
}