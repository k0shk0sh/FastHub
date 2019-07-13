package com.fastaccess.data.persistence.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.fastaccess.data.model.CountModel
import com.fastaccess.data.model.PageInfoModel
import com.google.gson.annotations.SerializedName
import github.GetProfileOrgsQuery

/**
 * Created by Kosh on 2018-11-26.
 */


data class OrgsModel(
    @SerializedName("totalCount") var totalCount: Int = 0,
    @SerializedName("orgs") var orgs: List<OrganizationModel>? = null,
    @Ignore @SerializedName("pageInfo") var pageInfo: PageInfoModel? = null
) {
    companion object {
        fun newInstance(response: GetProfileOrgsQuery.Data?): OrgsModel? {
            return response?.user?.organizations?.let { data ->
                OrgsModel(data.totalCount, OrganizationModel.newOrganizationInstance(data.nodes),
                    PageInfoModel(data.pageInfo.startCursor, data.pageInfo.endCursor,
                        data.pageInfo.isHasNextPage, data.pageInfo.isHasPreviousPage))
            }
        }
    }
}

@Entity(tableName = OrganizationModel.TABLE_NAME)
data class OrganizationModel(
    @PrimaryKey @SerializedName("id") var id: String,
    @SerializedName("databaseId") var databaseId: Int? = null,
    @SerializedName(value = "avatar_url", alternate = ["avatarUrl"]) var avatarUrl: String? = null,
    @SerializedName("location") var location: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("login") var login: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("url") var url: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("isVerified") var isVerified: Boolean? = null,
    @SerializedName("viewerIsAMember") var viewerIsAMember: Boolean? = null,
    @SerializedName("viewerCanCreateTeams") var viewerCanCreateTeams: Boolean? = null,
    @SerializedName("viewerCanCreateRepositories") var viewerCanCreateRepositories: Boolean? = null,
    @SerializedName("viewerCanCreateProjects") var viewerCanCreateProjects: Boolean? = null,
    @SerializedName("viewerCanAdminister") var viewerCanAdminister: Boolean? = null,
    @SerializedName("teams") @Embedded(prefix = "teams_") var teams: CountModel? = null,
    @SerializedName("projects") @Embedded(prefix = "projects_") var projects: CountModel? = null,
    @SerializedName("members") @Embedded(prefix = "members_") var members: CountModel? = null,
    @SerializedName("repositories") @Embedded(prefix = "repositories_") var repositories: CountModel? = null
) {
    companion object {
        const val TABLE_NAME = "orgs_table"

        internal fun newOrganizationInstance(list: List<GetProfileOrgsQuery.Node>?): List<OrganizationModel>? {
            return list?.asSequence()?.map { data ->
                OrganizationModel(data.id, data.databaseId, data.avatarUrl.toString(), data.location, data.email, data.login, data.name,
                    data.url.toString(), data.description, data.isVerified, data.isViewerIsAMember, data.isViewerCanCreateTeams,
                    data.isViewerCanCreateRepositories, data.isViewerCanCreateProjects, data.isViewerCanAdminister,
                    CountModel(data.teams.totalCount), CountModel(data.projects.totalCount), CountModel(data.membersWithRole.totalCount),
                    CountModel(data.repositories.totalCount))
            }?.toList() ?: arrayListOf()
        }
    }
}