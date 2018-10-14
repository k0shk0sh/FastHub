package com.fastaccess.data.model

import androidx.fragment.app.Fragment

/**
 * Created by Kosh on 05.10.18.
 */
data class ViewPagerModel(
        var text: CharSequence = "",
        val fragment: Fragment,
        var icon: Int? = null
)