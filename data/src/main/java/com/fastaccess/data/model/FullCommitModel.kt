package com.fastaccess.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class FullCommitModel(
    @SerializedName("id") var id: String? = null,
    @SerializedName("abbreviatedOid") var abbreviatedOid: String? = null,
    @SerializedName("oid") var oid: String? = null,
    @SerializedName("author") var author: ShortUserModel? = null,
    @SerializedName("messageHeadline") var messageHeadline: String? = null,
    @SerializedName("messageBody") var messageBody: String? = null,
    @SerializedName("commitUrl") var commitUrl: String? = null,
    @SerializedName("authoredDate") var authoredDate: Date? = null,
    @SerializedName("committedViaWeb") var committedViaWeb: Boolean? = null,
    @SerializedName("isVerified") var isVerified: Boolean? = null,
    @SerializedName("state") var state: String? = null,
    @SerializedName("changedFiles") var changedFiles: Int = 0,
    @SerializedName("additions") var additions: Int = 0,
    @SerializedName("deletions") var deletions: Int = 0
)