package com.fastaccess.domain.response

/**
 * Created by Kosh on 06.05.18.
 */
data class PageableResponse<M>(
    var first: Int = 0,
    var next: Int = 0,
    var previous: Int = 0,
    var last: Int = 0,
    var totalCount: Int = 0,
    var incompleteResults: Boolean = false,
    var items: ArrayList<M>? = null
)