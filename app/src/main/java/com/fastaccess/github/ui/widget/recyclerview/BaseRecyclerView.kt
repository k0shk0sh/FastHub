package com.fastaccess.github.ui.widget.recyclerview

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Kosh on 23.06.18.
 */
class BaseRecyclerView constructor(context: Context,
                                   attrs: AttributeSet? = null,
                                   defStyle: Int = 0) : RecyclerView(context, attrs, defStyle) {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
}