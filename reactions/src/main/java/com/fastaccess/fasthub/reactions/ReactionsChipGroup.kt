package com.fastaccess.fasthub.reactions

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.PopupWindow
import com.fastaccess.data.model.ReactionContent
import com.fastaccess.data.model.ReactionGroupModel
import com.fastaccess.data.model.getEmoji
import com.fastaccess.reactions.R
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.reactions_chips_layout.view.*

/**
 * Created by Kosh on 06.02.19.
 */
class ReactionsChipGroup : ChipGroup {
    constructor(context: Context?) : super(context)
    constructor(
        context: Context?,
        attrs: AttributeSet?
    ) : super(context, attrs)

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    @SuppressLint("RestrictedApi")
    override fun onFinishInflate() {
        super.onFinishInflate()
        View.inflate(context, R.layout.reactions_chips_layout, this)
    }

    @SuppressLint("SetTextI18n") fun setup(
        id: String,
        reactionGroups: List<ReactionGroupModel>?,
        popupWindow: PopupWindow? = null,
        callback: (() -> Unit)?
    ) {
        thumbsUp.text = "${ReactionContent.THUMBS_UP.getEmoji()} ${reactionGroups
            ?.firstOrNull { it.content == ReactionContent.THUMBS_UP }
            ?.users?.totalCount}"
        thumbsDown.text = "${ReactionContent.THUMBS_DOWN.getEmoji()} ${reactionGroups
            ?.firstOrNull { it.content == ReactionContent.THUMBS_DOWN }
            ?.users?.totalCount}"
        confused.text = "${ReactionContent.CONFUSED.getEmoji()} ${reactionGroups
            ?.firstOrNull { it.content == ReactionContent.CONFUSED }
            ?.users?.totalCount}"
        laugh.text = "${ReactionContent.LAUGH.getEmoji()} ${reactionGroups
            ?.firstOrNull { it.content == ReactionContent.LAUGH }
            ?.users?.totalCount}"
        hooray.text = "${ReactionContent.HOORAY.getEmoji()} ${reactionGroups
            ?.firstOrNull { it.content == ReactionContent.HOORAY }
            ?.users?.totalCount}"
        heart.text = "${ReactionContent.HEART.getEmoji()} ${reactionGroups
            ?.firstOrNull { it.content == ReactionContent.HEART }
            ?.users?.totalCount}"
        rocket.text = "${ReactionContent.ROCKET.getEmoji()} ${reactionGroups
            ?.firstOrNull { it.content == ReactionContent.ROCKET }
            ?.users?.totalCount}"
        eyes.text = "${ReactionContent.EYES.getEmoji()} ${reactionGroups
            ?.firstOrNull { it.content == ReactionContent.EYES }
            ?.users?.totalCount}"

        thumbsUp.setOnClickListener { react(id, reactionGroups?.firstOrNull { it.content == ReactionContent.THUMBS_UP }, popupWindow, callback) }
        thumbsDown.setOnClickListener { react(id, reactionGroups?.firstOrNull { it.content == ReactionContent.THUMBS_DOWN }, popupWindow, callback) }
        confused.setOnClickListener { react(id, reactionGroups?.firstOrNull { it.content == ReactionContent.CONFUSED }, popupWindow, callback) }
        laugh.setOnClickListener { react(id, reactionGroups?.firstOrNull { it.content == ReactionContent.LAUGH }, popupWindow, callback) }
        hooray.setOnClickListener { react(id, reactionGroups?.firstOrNull { it.content == ReactionContent.HOORAY }, popupWindow, callback) }
        heart.setOnClickListener { react(id, reactionGroups?.firstOrNull { it.content == ReactionContent.HEART }, popupWindow, callback) }
        rocket.setOnClickListener { react(id, reactionGroups?.firstOrNull { it.content == ReactionContent.ROCKET }, popupWindow, callback) }
        eyes.setOnClickListener { react(id, reactionGroups?.firstOrNull { it.content == ReactionContent.EYES }, popupWindow, callback) }
    }

    private fun react(
        id: String,
        model: ReactionGroupModel?,
        popupWindow: PopupWindow?,
        callback: (() -> Unit)?
    ) {
        model?.let { reaction ->
            val add: Boolean
            if (reaction.viewerHasReacted == true) {
                reaction.users?.totalCount = reaction.users?.totalCount?.minus(1)
                model.viewerHasReacted = false
                add = false
            } else {
                reaction.users?.totalCount = reaction.users?.totalCount?.plus(1)
                model.viewerHasReacted = true
                add = true
            }
            ReactionWorker.enqueue(model.content?.value ?: "", id, add)
            callback?.invoke()
            popupWindow?.dismiss()
        }
    }
}