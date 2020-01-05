package com.fastaccess.data.persistence.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fastaccess.data.model.*
import com.fastaccess.data.model.parcelable.LabelModel
import com.fastaccess.data.model.parcelable.MilestoneModel
import com.google.gson.annotations.SerializedName
import java.util.*


/**
 * Created by Kosh on 27.01.19.
 */
@Entity(tableName = PullRequestModel.TABLE_NAME)
data class PullRequestModel(
    @PrimaryKey @SerializedName("id") var id: String,
    @SerializedName("databaseId") var databaseId: Int? = null,
    @SerializedName("number") var number: Int? = null,
    @SerializedName("activeLockReason") var activeLockReason: String? = null,
    @SerializedName("body") var body: String? = null,
    @SerializedName("bodyHTML") var bodyHTML: String? = null,
    @SerializedName("closedAt") var closedAt: Date? = null,
    @SerializedName("createdAt") var createdAt: Date? = null,
    @SerializedName("updatedAt") var updatedAt: Date? = null,
    @SerializedName("state") var state: String? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("viewerSubscription") var viewerSubscription: String? = null,
    @SerializedName("author") @Embedded(prefix = "author_") var author: ShortUserModel? = null,
    @SerializedName("repository") @Embedded(prefix = "repo_") var repo: EmbeddedRepoModel? = null,
    @SerializedName("mergedBy") @Embedded(prefix = "mergedby_") var mergedBy: ShortUserModel? = null,
    @SerializedName("userContentEdits") @Embedded(prefix = "user_content_edits_") var userContentEdits: CountModel? = CountModel(),
    @SerializedName("reactionGroups") var reactionGroups: List<ReactionGroupModel>? = null,
    @SerializedName("viewerCannotUpdateReasons") var viewerCannotUpdateReasons: List<String>? = null,
    @SerializedName("closed") var closed: Boolean? = false,
    @SerializedName("createdViaEmail") var createdViaEmail: Boolean? = false,
    @SerializedName("locked") var locked: Boolean? = false,
    @SerializedName("viewerCanReact") var viewerCanReact: Boolean? = false,
    @SerializedName("viewerCanSubscribe") var viewerCanSubscribe: Boolean? = false,
    @SerializedName("viewerCanUpdate") var viewerCanUpdate: Boolean? = false,
    @SerializedName("viewerDidAuthor") var viewerDidAuthor: Boolean? = false,
    @SerializedName("mergeable") var mergeable: String? = null,
    @SerializedName("merged") var merged: Boolean? = false,
    @SerializedName("mergedAt") var mergedAt: Date? = null,
    @SerializedName("authorAssociation") var authorAssociation: String? = null,
    @SerializedName("url") var url: String? = null,
    @SerializedName("labels") var labels: List<LabelModel>? = null,
    @SerializedName("milestone") @Embedded(prefix = "milestone_") var milestone: MilestoneModel? = null,
    @SerializedName("assignees") var assignees: List<ShortUserModel>? = null,
    @SerializedName("headRefName") var headRefName: String? = null,
    @SerializedName("baseRefName") var baseRefName: String? = null,
    @SerializedName("reviewDashboard") @Embedded(prefix = "dashboard_") var dashboard: PullRequestDashboard? = null
) {
    companion object {
        const val TABLE_NAME = "pullrequest_table"
    }
}
