package com.fastaccess.data.model

import com.fastaccess.data.persistence.models.IssueModel
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Created by Kosh on 31.01.19.
 */
data class TimelineModel(
    @SerializedName("issue") var issue: IssueModel? = null,
    @SerializedName("commit") var commit: CommitModel? = null,
    @SerializedName("comment") var comment: CommentModel? = null
)

data class CommitModel(
    @SerializedName("id") var id: String? = null,
    @SerializedName("author") var author: ShortUserModel? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("abbreviatedOid") var abbreviatedOid: String? = null,
    @SerializedName("oid") var oid: String? = null,
    @SerializedName("commitUrl") var commitUrl: String? = null,
    @SerializedName("authoredDate") var authoredDate: Date? = null,
    @SerializedName("committedViaWeb") var committedViaWeb: Boolean? = null
)

data class CommentModel(
    @SerializedName("id") var id: String? = null,
    @SerializedName("author") var author: ShortUserModel? = null,
    @SerializedName("bodyHTML") var bodyHTML: String? = null,
    @SerializedName("body") var body: String? = null,
    @SerializedName("authorAssociation") var authorAssociation: CommentAuthorAssociation? = null,
    @SerializedName("viewerCannotUpdateReasons") var viewerCannotUpdateReasons: List<CommentCannotUpdateReason?>? = null,
    @SerializedName("reactionGroups") var reactionGroups: List<ReactionGroupModel>? = null,
    @SerializedName("createdAt") var createdAt: Date? = null,
    @SerializedName("updatedAt") var updatedAt: Date? = null,
    @SerializedName("viewerCanReact") var viewerCanReact: Boolean? = null,
    @SerializedName("viewerCanDelete") var viewerCanDelete: Boolean? = null,
    @SerializedName("viewerCanUpdate") var viewerCanUpdate: Boolean? = null,
    @SerializedName("viewerDidAuthor") var viewerDidAuthor: Boolean? = null,
    @SerializedName("viewerCanMinimize") var viewerCanMinimize: Boolean? = null
)

enum class CommentAuthorAssociation(val value: String) {
    MEMBER("MEMBER"),
    OWNER("OWNER"),
    COLLABORATOR("COLLABORATOR"),
    CONTRIBUTOR("CONTRIBUTOR"),
    FIRST_TIME_CONTRIBUTOR("FIRST_TIME_CONTRIBUTOR"),
    FIRST_TIMER("FIRST_TIMER"),
    NONE("NONE");

    companion object {
        fun fromName(name: String): CommentAuthorAssociation? = values().firstOrNull { it.value == name }
    }
}

enum class CommentCannotUpdateReason(val value: kotlin.String) {
    INSUFFICIENT_ACCESS("INSUFFICIENT_ACCESS"),
    LOCKED("LOCKED"),
    LOGIN_REQUIRED("LOGIN_REQUIRED"),
    MAINTENANCE("LOGIN_REQUIRED"),
    VERIFIED_EMAIL_REQUIRED("LOGIN_REQUIRED"),
    DENIED("LOGIN_REQUIRED");

    companion object {
        fun fromName(name: String): CommentCannotUpdateReason? = CommentCannotUpdateReason.values().firstOrNull { it.value == name }
    }
}