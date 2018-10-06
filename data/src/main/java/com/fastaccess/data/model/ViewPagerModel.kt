package com.fastaccess.data.model

import androidx.fragment.app.Fragment

/**
 * Created by Kosh on 05.10.18.
 */
data class ViewPagerModel(
        val text: String = "",
        val fragment: Fragment,
        val icon: Int? = null
)