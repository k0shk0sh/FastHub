package com.fastaccess.domain.response.body

import com.google.gson.annotations.SerializedName

/**
 * Created by Kosh on 30.03.19.
 */
data class LabelsBodyModel(
    @SerializedName("labels") var labels: List<String>? = null
)