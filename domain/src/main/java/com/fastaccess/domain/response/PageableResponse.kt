package com.fastaccess.domain.response

/**
 * Created by Kosh on 06.05.18.
 */
sealed class PageableResponse {

    data class Success<M>(var first: Int = 0,
                          var next: Int = 0,
                          var previous: Int = 0,
                          var last: Int = 0,
                          var totalCout: Int = 0,
                          var incompleteResults: Boolean = false,
                          var items: ArrayList<M>? = null) : PageableResponse()

    object Failed : PageableResponse()
}