package com.fastaccess.domain.response.body

import com.google.gson.annotations.SerializedName

/**
 * Created by Kosh on 30.03.19.
 */
data class MilestoneBodyModel(
    @SerializedName("title") var title: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("due_on") var dueOn: String? = null
)