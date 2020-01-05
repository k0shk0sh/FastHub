package com.fastaccess.data.model

import com.google.gson.annotations.SerializedName

data class RepoLanguageModel(
        @SerializedName("name") var name: String? = null,
        @SerializedName("color") var color: String? = null
)