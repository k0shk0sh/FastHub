package com.fastaccess.domain.response

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Created by Kosh on 19.06.18.
 */

data class NotificationResponse(
        @SerializedName("id") var id: String? = null,
        @SerializedName("repository") var repository: RepositoryResponse? = null,
        @SerializedName("subject") var subject: NotificationSubject? = null,
        @SerializedName("reason") var reason: String? = null,
        @SerializedName("unread") var unread: Boolean? = null,
        @SerializedName("updated_at") var updatedAt: Date? = null,
        @SerializedName("last_read_at") var lastReadAt: Date? = null,
        @SerializedName("url") var url: String? = null,
        @SerializedName("login") var login: String? = null
)

data class NotificationSubject(
        @SerializedName("title") var title: String? = null,
        @SerializedName("url") var url: String? = null,
        @SerializedName("latest_comment_url") var latestCommentUrl: String? = null,
        @SerializedName("type") var type: String? = null
)