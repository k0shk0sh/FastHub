package com.fastaccess.data.model

import androidx.fragment.app.Fragment

/**
 * Created by Kosh on 05.10.18.
 */
data class ViewPagerModel(
        val icon: Int? = null,
        val text: String = "",
        val fragment: Fragment
)