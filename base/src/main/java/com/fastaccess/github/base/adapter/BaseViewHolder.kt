package com.fastaccess.github.base.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fastaccess.github.base.widget.recyclerview.decoration.InsetDividerDecoration

/**
 * Created by Kosh on 23.06.18.
 */
abstract class BaseViewHolder<T>(view: View) : RecyclerView.ViewHolder(view), InsetDividerDecoration.HasDivider {
    abstract fun bind(item: T)
    override fun canDivide(): Boolean = true
    open fun onDetached() = Unit
}