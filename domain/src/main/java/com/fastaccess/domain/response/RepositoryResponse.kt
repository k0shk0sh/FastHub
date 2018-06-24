package com.fastaccess.domain.response

import com.google.gson.annotations.SerializedName

data class RepositoryResponse(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("node_id") var nodeId: String? = null,
        @SerializedName("owner") var owner: UserResponse? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("full_name") var fullName: String? = null,
        @SerializedName("description") var description: String? = null,
        @SerializedName("private") var private: Boolean? = null,
        @SerializedName("fork") var fork: Boolean? = null,
        @SerializedName("url") var url: String? = null,
        @SerializedName("html_url") var htmlUrl: String? = null
)