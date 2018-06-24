package com.fastaccess.domain.response

import com.fastaccess.domain.response.enums.EventsType
import com.google.gson.annotations.SerializedName
import java.util.*


/**
 * Created by Kosh on 23.06.18.
 */
data class FeedResponse(@SerializedName("id") var id: Long = 0L,
                        @SerializedName("type") var type: EventsType? = null,
                        @SerializedName("created_at") var createdAt: Date? = null,
                        @SerializedName("public") var isPublic: Boolean? = null,
                        @SerializedName("actor") var actor: UserResponse? = null,
                        @SerializedName("repo") var repo: RepositoryResponse? = null,
                        @SerializedName("payload") var payload: PayloadResponse? = null,
                        @SerializedName("login") var login: String? = null)

data class PayloadResponse(@SerializedName("action") var action: String? = null,
                           @SerializedName("forkee") var forkee: RepositoryResponse? = null,
                           @SerializedName("ref_type") var refType: String? = null,
                           @SerializedName("target") var target: UserResponse? = null,
                           @SerializedName("member") var member: UserResponse? = null,
                           @SerializedName("description") var description: String? = null,
                           @SerializedName("before") var before: String? = null,
                           @SerializedName("head") var head: String? = null,
                           @SerializedName("ref") var ref: String? = null,
                           @SerializedName("size") var size: Int? = 0,
                           @SerializedName("user") var user: UserResponse? = null,
                           @SerializedName("blocked_user") var blockedUser: UserResponse? = null,
                           @SerializedName("organization") var organization: UserResponse? = null,
                           @SerializedName("invitation") var invitation: UserResponse? = null)