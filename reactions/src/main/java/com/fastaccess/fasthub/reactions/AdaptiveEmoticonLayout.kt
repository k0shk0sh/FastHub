package com.fastaccess.fasthub.reactions

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.fastaccess.data.model.ReactionContent
import com.fastaccess.data.model.ReactionGroupModel
import com.fastaccess.data.model.getEmoji
import com.fastaccess.github.base.extensions.popupEmoji
import com.fastaccess.github.extensions.getColorAttr
import com.fastaccess.reactions.R
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.add_reactions_layout.view.*

/**
 * Created by Kosh on 2019-07-30.
 */
class AdaptiveEmoticonLayout : LinearLayout {
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
        View.inflate(context, R.layout.add_reactions_layout, this)
    }

    fun init(
        id: String,
        reactionGroups: List<ReactionGroupModel>?,
        callback: (() -> Unit)?
    ) {
        addEmoji.setOnClickListener {
            it.popupEmoji(id, reactionGroups) {
                initReactions(reactionGroups)
                callback?.invoke()
            }
        }

        initReactions(reactionGroups)

        thumbsUp.setOnClickListener { react(it, id, reactionGroups?.firstOrNull { it.content == ReactionContent.THUMBS_UP }, callback) }
        thumbsDown.setOnClickListener { react(it, id, reactionGroups?.firstOrNull { it.content == ReactionContent.THUMBS_DOWN }, callback) }
        confused.setOnClickListener { react(it, id, reactionGroups?.firstOrNull { it.content == ReactionContent.CONFUSED }, callback) }
        laugh.setOnClickListener { react(it, id, reactionGroups?.firstOrNull { it.content == ReactionContent.LAUGH }, callback) }
        hooray.setOnClickListener { react(it, id, reactionGroups?.firstOrNull { it.content == ReactionContent.HOORAY }, callback) }
        heart.setOnClickListener { react(it, id, reactionGroups?.firstOrNull { it.content == ReactionContent.HEART }, callback) }
        rocket.setOnClickListener { react(it, id, reactionGroups?.firstOrNull { it.content == ReactionContent.ROCKET }, callback) }
        eyes.setOnClickListener { react(it, id, reactionGroups?.firstOrNull { it.content == ReactionContent.EYES }, callback) }
    }


    @SuppressLint("SetTextI18n")
    fun initReactions(
        reactionGroups: List<ReactionGroupModel>?
    ) {
        getReactionCount(reactionGroups, ReactionContent.THUMBS_UP).let {
            if (it.first > 0) {
                thumbsUp.isVisible = true
                thumbsUp.text = "${ReactionContent.THUMBS_UP.getEmoji()} ${it.first}"
                highlightChip(thumbsUp, it.second?.viewerHasReacted == true)

            } else {
                thumbsUp.isVisible = false
            }
        }

        getReactionCount(reactionGroups, ReactionContent.THUMBS_DOWN).let {
            if (it.first > 0) {
                thumbsDown.isVisible = true
                thumbsDown.text = "${ReactionContent.THUMBS_DOWN.getEmoji()} ${it.first}"
                highlightChip(thumbsDown, it.second?.viewerHasReacted == true)
            } else {
                thumbsDown.isVisible = false
            }
        }

        getReactionCount(reactionGroups, ReactionContent.CONFUSED).let {
            if (it.first > 0) {
                confused.isVisible = true
                confused.text = "${ReactionContent.CONFUSED.getEmoji()} ${it.first}"
                highlightChip(confused, it.second?.viewerHasReacted == true)
            } else {
                confused.isVisible = false
            }
        }

        getReactionCount(reactionGroups, ReactionContent.LAUGH).let {
            if (it.first > 0) {
                laugh.isVisible = true
                laugh.text = "${ReactionContent.LAUGH.getEmoji()} ${it.first}"
                highlightChip(laugh, it.second?.viewerHasReacted == true)
            } else {
                laugh.isVisible = false
            }
        }

        getReactionCount(reactionGroups, ReactionContent.HOORAY).let {
            if (it.first > 0) {
                hooray.isVisible = true
                hooray.text = "${ReactionContent.HOORAY.getEmoji()} ${it.first}"
                highlightChip(hooray, it.second?.viewerHasReacted == true)
            } else {
                hooray.isVisible = false
            }
        }

        getReactionCount(reactionGroups, ReactionContent.HEART).let {
            if (it.first > 0) {
                heart.isVisible = true
                heart.text = "${ReactionContent.HEART.getEmoji()} ${it.first}"
                highlightChip(heart, it.second?.viewerHasReacted == true)
            } else {
                heart.isVisible = false
            }
        }

        getReactionCount(reactionGroups, ReactionContent.ROCKET).let {
            if (it.first > 0) {
                rocket.isVisible = true
                rocket.text = "${ReactionContent.ROCKET.getEmoji()} ${it.first}"
                highlightChip(rocket, it.second?.viewerHasReacted == true)
            } else {
                rocket.isVisible = false
            }
        }

        getReactionCount(reactionGroups, ReactionContent.EYES).let {
            if (it.first > 0) {
                eyes.isVisible = true
                eyes.text = "${ReactionContent.EYES.getEmoji()} ${it.first}"
                highlightChip(eyes, it.second?.viewerHasReacted == true)
            } else {
                eyes.isVisible = false
            }
        }
    }

    private fun highlightChip(
        chip: Chip,
        didReaction: Boolean
    ) {
        chip.chipStrokeWidth = if (didReaction) 1f else 0f
        chip.chipStrokeColor = ColorStateList.valueOf(
            if (didReaction) {
                context.getColorAttr(R.attr.colorAccent)
            } else {
                Color.TRANSPARENT
            }
        )
    }

    private fun getReactionCount(
        reactionGroups: List<ReactionGroupModel>?,
        content: ReactionContent
    ): Pair<Int, ReactionGroupModel?> = reactionGroups
        ?.filter { it.users != null && it.content != null }
        ?.firstOrNull { it.content == content }?.let {
            return@let Pair(it.users?.totalCount ?: 0, it)
        } ?: Pair(0, null)

    private fun react(
        view: View,
        id: String,
        model: ReactionGroupModel?,
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
            if (reaction.users?.totalCount == 0) {
                view.isVisible = false
            }
            ReactionWorker.enqueue(model.content?.value ?: "", id, add)
            callback?.invoke()
        }
    }

}