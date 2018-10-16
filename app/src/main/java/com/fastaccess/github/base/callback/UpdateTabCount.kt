package com.fastaccess.github.base.callback

import com.fastaccess.data.model.FragmentType

/**
 * Created by Kosh on 16.10.18.
 */
interface UpdateTabCount {
    fun updateCount(type: FragmentType, count: Long)
}