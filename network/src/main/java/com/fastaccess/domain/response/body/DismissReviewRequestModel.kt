package com.fastaccess.domain.response.body

import com.google.gson.annotations.SerializedName

/**
 * Created by Kosh on 2019-07-18.
 */
data class DismissReviewRequestModel(
    @SerializedName("message") var message: String
)