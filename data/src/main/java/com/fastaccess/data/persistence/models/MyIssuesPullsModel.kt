package com.fastaccess.data.persistence.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fastaccess.data.persistence.models.MyIssuesPullsModel.Companion.TABLE_NAME
import com.google.gson.annotations.SerializedName

/**
 * Created by Kosh on 17.06.18.
 */
@Entity(tableName = TABLE_NAME)
data class MyIssuesPullsModel(
    @PrimaryKey @SerializedName("id") var id: String = "",
    @SerializedName("databaseId") var databaseId: Int? = null,
    @SerializedName("number") var number: Int? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("repoName") var repoName: String? = null,
    @SerializedName("commentsCount") var commentCounts: Int? = null,
    @SerializedName("state") var state: String? = null,
    @SerializedName("url") var url: String? = null,
    @SerializedName("isPr") var isPr: Boolean = false
) {
    companion object {
        const val TABLE_NAME = "main_screen_issues"
    }
}