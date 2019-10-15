package com.fastaccess.domain.response.body

import com.google.gson.annotations.SerializedName

/**
 * Created by Kosh on 2019-07-18.
 */
data class CommentRequestModel(
    @SerializedName("body") var body: String
)