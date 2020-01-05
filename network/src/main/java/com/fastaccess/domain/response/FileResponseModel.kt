package com.fastaccess.domain.response

import com.google.gson.annotations.SerializedName

data class FileResponseModel(
    @SerializedName("patch") var patch: String? = null,
    @SerializedName("filename") var filename: String? = null,
    @SerializedName("additions") var additions: Int? = null,
    @SerializedName("deletions") var deletions: Int? = null,
    @SerializedName("changes") var changes: Int? = null,
    @SerializedName("sha") var sha: String? = null,
    @SerializedName("blob_url") var blobUrl: String? = null,
    @SerializedName("raw_url") var rawUrl: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("contents_url") var contentsUrl: String? = null
)