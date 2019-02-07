package com.fastaccess.github.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.fastaccess.data.model.ReactionContent
import com.fastaccess.data.model.ReactionGroupModel
import com.fastaccess.github.R
import com.fastaccess.github.platform.works.ReactionWorker
import com.fastaccess.markdown.extension.*
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.reactions_chips_layout.view.*

/**
 * Created by Kosh on 06.02.19.
 */
class ReactionsChipGroup : ChipGroup {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @SuppressLint("RestrictedApi")
    override fun onFinishInflate() {
        super.onFinishInflate()
        View.inflate(context, R.layout.reactions_chips_layout, this)
        isSingleLine = true
    }

    @SuppressLint("SetTextI18n") fun setup(
        id: String,
        reactionGroups: List<ReactionGroupModel>?,
        callback: (() -> Unit)?) {
        thumbsUp.text = "${getThumbsUpEmoji()} ${reactionGroups
            ?.firstOrNull { it.content == ReactionContent.THUMBS_UP }
            ?.users?.totalCount}"
        thumbsDown.text = "${getThumbsDownEmoji()} ${reactionGroups
            ?.firstOrNull { it.content == ReactionContent.THUMBS_DOWN }
            ?.users?.totalCount}"
        confused.text = "${getSadEmoji()} ${reactionGroups
            ?.firstOrNull { it.content == ReactionContent.CONFUSED }
            ?.users?.totalCount}"
        laugh.text = "${getLaughEmoji()} ${reactionGroups
            ?.firstOrNull { it.content == ReactionContent.LAUGH }
            ?.users?.totalCount}"
        hooray.text = "${getHoorayEmoji()} ${reactionGroups
            ?.firstOrNull { it.content == ReactionContent.HOORAY }
            ?.users?.totalCount}"
        heart.text = "${getHeartEmoji()} ${reactionGroups
            ?.firstOrNull { it.content == ReactionContent.HEART }
            ?.users?.totalCount}"

        thumbsUp.setOnClickListener { react(id, reactionGroups?.firstOrNull { it.content == ReactionContent.THUMBS_UP }, callback) }
        thumbsDown.setOnClickListener { react(id, reactionGroups?.firstOrNull { it.content == ReactionContent.THUMBS_DOWN }, callback) }
        confused.setOnClickListener { react(id, reactionGroups?.firstOrNull { it.content == ReactionContent.CONFUSED }, callback) }
        laugh.setOnClickListener { react(id, reactionGroups?.firstOrNull { it.content == ReactionContent.LAUGH }, callback) }
        hooray.setOnClickListener { react(id, reactionGroups?.firstOrNull { it.content == ReactionContent.HOORAY }, callback) }
        heart.setOnClickListener { react(id, reactionGroups?.firstOrNull { it.content == ReactionContent.HEART }, callback) }
    }

    private fun react(
        id: String,
        model: ReactionGroupModel?,
        callback: (() -> Unit)?) {
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
        }
    }
}