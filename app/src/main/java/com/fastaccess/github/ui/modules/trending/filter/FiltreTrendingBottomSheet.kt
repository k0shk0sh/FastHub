package com.fastaccess.github.ui.modules.trending.filter

/**
 * Created by Kosh on 23.01.19.
 */
class FiltreTrendingBottomSheet {


    interface FilterTrendingCallback {
        fun onFilterApplied(lan: String, since: String)
    }
}