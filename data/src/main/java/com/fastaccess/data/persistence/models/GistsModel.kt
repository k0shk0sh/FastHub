package com.fastaccess.data.persistence.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.fastaccess.data.model.CountModel
import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.model.SimpleUserModel
import com.google.gson.annotations.SerializedName
import github.GetProfileGistsQuery
import java.util.*

data class GistsModel(
        @SerializedName("totalCount") var totalCount: Int = 0,
        @SerializedName("gists") var gists: List<ProfileGistModel>? = null,
        @Ignore @SerializedName("pageInfo") var pageInfo: PageInfoModel? = null
) {
    companion object {
        fun newInstance(response: GetProfileGistsQuery.Data?, login: String): GistsModel? {
            return response?.user?.gists?.let { gists ->
                GistsModel(gists.totalCount, ProfileGistModel.newInstances(gists.nodes, login),
                        PageInfoModel(gists.pageInfo.startCursor, gists.pageInfo.endCursor,
                                gists.pageInfo.isHasNextPage, gists.pageInfo.isHasPreviousPage))
            }
        }
    }
}

@Entity(tableName = ProfileGistModel.TABLE_NAME)
data class ProfileGistModel(
        @PrimaryKey @SerializedName("id") var id: String,
        @SerializedName("name") var name: String? = null,
        @SerializedName("description") var description: String? = null,
        @SerializedName("public") var isPublic: Boolean? = null,
        @SerializedName("owner") @Embedded(prefix = "owner_") var owner: SimpleUserModel? = null,
        @SerializedName("updated_at") var updatedAt: Date? = null,
        @SerializedName("comments") @Embedded(prefix = "comments_") var comments: CountModel? = null,
        @SerializedName("stargazers") @Embedded(prefix = "stargazers_") var stargazers: CountModel? = null,
        @SerializedName("viewerHasStarred") var viewerHasStarred: Boolean? = null,
        @SerializedName("login") var login: String? = null
) {
    companion object {
        const val TABLE_NAME = "profile_gist_table"

        internal fun newInstances(list: List<GetProfileGistsQuery.Node>?, login: String): List<ProfileGistModel> {
            return list?.asSequence()?.map { data ->
                ProfileGistModel(data.id, data.name, data.description, data.isPublic, SimpleUserModel(data.owner?.login),
                        data.updatedAt, CountModel(data.comments.totalCount), CountModel(data.stargazers.totalCount),
                        data.isViewerHasStarred, login)
            }?.toList() ?: arrayListOf()
        }
    }
}