package com.fastaccess.data.persistence.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fastaccess.data.persistence.models.NotificationModel.Companion.TABLE_NAME
import com.fastaccess.domain.response.NotificationResponse
import com.fastaccess.domain.response.UserResponse
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.util.*

/**
 * Created by Kosh on 22.06.18.
 */
@Entity(tableName = TABLE_NAME)
data class NotificationModel(@PrimaryKey @SerializedName("id") var id: String = "",
                             @Embedded(prefix = "repo_") @SerializedName("repository") var repository: NotificationRepository? = null,
                             @Embedded(prefix = "subject") @SerializedName("subject") var subject: NotificationSubject? = null,
                             @SerializedName("reason") var reason: String? = null,
                             @SerializedName("unread") var unread: Boolean? = null,
                             @SerializedName("updated_at") var updatedAt: Date? = null,
                             @SerializedName("last_read_at") var lastReadAt: Date? = null,
                             @SerializedName("url") var url: String? = null) {
    companion object {
        const val TABLE_NAME = "notification_table"

        fun convert(gson: Gson, response: NotificationResponse): NotificationModel {
            return gson.fromJson(gson.toJson(response), NotificationModel::class.java)
        }

        fun convert(gson: Gson, response: List<NotificationResponse>): List<NotificationModel> {
            return gson.fromJson(gson.toJson(response), object : TypeToken<List<NotificationModel>>() {}.type)
        }
    }
}

data class NotificationSubject(
        @SerializedName("title") var title: String? = null,
        @SerializedName("url") var url: String? = null,
        @SerializedName("latest_comment_url") var latestCommentUrl: String? = null,
        @SerializedName("type") var type: String? = null
)

data class NotificationRepository(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("node_id") var nodeId: String? = null,
        @Embedded(prefix = "owner_") @SerializedName("owner") var owner: UserResponse? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("full_name") var fullName: String? = null,
        @SerializedName("description") var description: String? = null,
        @SerializedName("private") var private: Boolean? = null,
        @SerializedName("fork") var fork: Boolean? = null,
        @SerializedName("url") var url: String? = null,
        @SerializedName("html_url") var htmlUrl: String? = null
)