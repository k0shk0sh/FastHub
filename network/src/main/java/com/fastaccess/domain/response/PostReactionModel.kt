package com.fastaccess.domain.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Kosh on 06.02.19.
 */
data class PostReactionModel(@SerializedName("content") var content: String)