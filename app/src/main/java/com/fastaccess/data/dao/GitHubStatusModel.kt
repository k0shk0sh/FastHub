package com.fastaccess.data.dao

import com.google.gson.annotations.SerializedName

/**
 * Created by Hashemsergani on 18.10.17.
 */
data class GitHubStatusModel(
    @SerializedName("status") var status: GithubStatus? = null
)

data class GithubStatus(
    @SerializedName("description") var description: String? = null,
    @SerializedName("indicator") var indicator: String? = null
)