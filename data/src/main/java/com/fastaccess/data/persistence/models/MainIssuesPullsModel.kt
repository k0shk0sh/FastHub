package com.fastaccess.data.persistence.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fastaccess.data.persistence.models.MainIssuesPullsModel.Companion.TABLE_NAME
import com.google.gson.annotations.SerializedName

/**
 * Created by Kosh on 17.06.18.
 */
@Entity(tableName = TABLE_NAME)
data class MainIssuesPullsModel(@PrimaryKey @SerializedName("id") var id: String = "",
                                @SerializedName("databaseId") var databaseId: Long? = null,
                                @SerializedName("number") var number: Long? = null,
                                @SerializedName("title") var title: String? = null,
                                @SerializedName("repoName") var repoName: String? = null,
                                @SerializedName("commentsCount") var commentCounts: Long? = null,
                                @SerializedName("state") var state: String? = null,
                                @SerializedName("login") var login: String? = null) {
    companion object {
        const val TABLE_NAME = "main_screen_issues"
    }
}