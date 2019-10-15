package com.fastaccess.domain.response

import com.google.gson.annotations.SerializedName

data class CommitFilesResponseModel(
    @SerializedName("files") var files: List<FileResponseModel>? = null
)