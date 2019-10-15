package com.fastaccess.domain.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Kosh on 2019-07-29.
 */
data class ImgureResponseModel(
    @SerializedName("success") var isSuccess: Boolean? = null,
    @SerializedName("status") var status: Int? = null,
    @SerializedName("data") var data: ImgurImage? = null
)

data class ImgurImage(
    @SerializedName("title") var title: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("link") var link: String? = null
)