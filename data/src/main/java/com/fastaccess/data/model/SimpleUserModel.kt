package com.fastaccess.data.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Kosh on 14.10.18.
 */
data class SimpleUserModel(@SerializedName("login") var login: String? = null)