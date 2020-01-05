package com.fastaccess.data.persistence.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.fastaccess.data.model.PageInfoModel
import com.google.gson.annotations.SerializedName
import github.GetProfileFollowersQuery
import github.GetProfileFollowingQuery
import github.fragment.ShortUserRowItem

/**
 * Created by Kosh on 15.10.18.
 */

data class FollowingsFollowersModel(
        @SerializedName("totalCount") var totalCount: Int = 0,
        @SerializedName("users") var users: List<FollowingFollowerModel>? = null,
        @Ignore @SerializedName("pageInfo") var pageInfo: PageInfoModel? = null
) {
    companion object {
        fun newFollowersInstance(response: GetProfileFollowersQuery.Data?, login: String): FollowingsFollowersModel? {
            return response?.user?.followers?.let { data ->
                FollowingsFollowersModel(data.totalCount, FollowingFollowerModel.newFollowersInstance(data.nodes, login),
                        PageInfoModel(data.pageInfo.startCursor, data.pageInfo.endCursor,
                                data.pageInfo.isHasNextPage, data.pageInfo.isHasPreviousPage))
            }
        }

        fun newFollowingInstance(response: GetProfileFollowingQuery.Data?, login: String): FollowingsFollowersModel? {
            return response?.user?.following?.let { data ->
                FollowingsFollowersModel(data.totalCount, FollowingFollowerModel.newFollowingInstance(data.nodes, login),
                        PageInfoModel(data.pageInfo.startCursor, data.pageInfo.endCursor,
                                data.pageInfo.isHasNextPage, data.pageInfo.isHasPreviousPage))
            }
        }
    }
}

@Entity(tableName = FollowingFollowerModel.TABLE_NAME)
data class FollowingFollowerModel(
        @SerializedName("login") var login: String? = null,
        @SerializedName("url") var url: String? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("location") var location: String? = null,
        @SerializedName("bio") var bio: String? = null,
        @SerializedName(value = "avatar_url", alternate = ["avatarUrl"]) var avatarUrl: String? = null,
        @SerializedName("viewerCanFollow") var viewerCanFollow: Boolean? = null,
        @SerializedName("viewerIsFollowing") var viewerIsFollowing: Boolean? = null,
        @SerializedName("currentLogin") var currentLogin: String? = null,
        @SerializedName("isFollowers") var isFollowers: Boolean = false
) {

    @PrimaryKey(autoGenerate = true) var id: Long = 0

    companion object {
        const val TABLE_NAME = "profile_follower_followings_table"

        internal fun newFollowersInstance(list: List<GetProfileFollowersQuery.Node>?, login: String): List<FollowingFollowerModel> {
            return list?.asSequence()?.map { data ->
                createUserInstance(data.fragments.shortUserRowItem, login, true)
            }?.toList() ?: arrayListOf()
        }

        internal fun newFollowingInstance(list: List<GetProfileFollowingQuery.Node>?, login: String): List<FollowingFollowerModel> {
            return list?.asSequence()?.map { data ->
                createUserInstance(data.fragments.shortUserRowItem, login, false)
            }?.toList() ?: arrayListOf()
        }

        private fun createUserInstance(user: ShortUserRowItem, login: String, isFollowers: Boolean) =
                FollowingFollowerModel(user.login, user.url.toString(), user.name, user.location, user.bio,
                        user.avatarUrl.toString(), user.isViewerCanFollow, user.isViewerIsFollowing, login, isFollowers)
    }
}