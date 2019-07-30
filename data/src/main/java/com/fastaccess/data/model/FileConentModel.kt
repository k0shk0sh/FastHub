package com.fastaccess.data.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Kosh on 2019-07-30.
 */
data class FileConentModel(
    @SerializedName("text") var text: String? = null,
    @SerializedName("isBinary") var isBinary: Boolean? = null,
    @SerializedName("isTruncated") var isTruncated: Boolean? = null,
    @SerializedName("byteSize") var byteSize: Int? = null
)