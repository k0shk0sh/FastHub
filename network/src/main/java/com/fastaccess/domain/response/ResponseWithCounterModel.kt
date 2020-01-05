package com.fastaccess.domain.response

data class ResponseWithCounterModel<T>(
    val totalCount: Int,
    val t: List<T>
)