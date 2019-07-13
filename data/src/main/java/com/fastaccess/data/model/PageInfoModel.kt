package com.fastaccess.data.model

/**
 * Created by Kosh on 08.10.18.
 */
data class PageInfoModel(
        val startCursor: String? = null,
        val endCursor: String? = null,
        val hasNextPage: Boolean = false,
        val hasPreviousPage: Boolean = false
)