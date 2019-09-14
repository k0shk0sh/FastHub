package com.fastaccess.fasthub.reactions

import android.view.View
import android.widget.PopupWindow
import com.fastaccess.data.model.ReactionGroupModel
import com.fastaccess.github.extensions.getDrawableCompat
import com.fastaccess.reactions.R
import kotlinx.android.synthetic.main.reaction_group_chip_widget.view.*

fun View.popupEmoji(
    id: String,
    list: List<ReactionGroupModel>?,
    callback: (() -> Unit)?
) {
    val popupWindow = PopupWindow(context)
    val view = View.inflate(context, R.layout.reaction_group_chip_widget, null)
    view.reactionGroup.setup(id, list, popupWindow, callback)
    popupWindow.contentView = view
    popupWindow.setBackgroundDrawable(context.getDrawableCompat(R.drawable.popup_window_background))
    popupWindow.elevation = resources.getDimension(R.dimen.spacing_normal)
    popupWindow.isOutsideTouchable = true
    popupWindow.isFocusable = true
    popupWindow.isTouchable = true
    popupWindow.showAsDropDown(this)
}