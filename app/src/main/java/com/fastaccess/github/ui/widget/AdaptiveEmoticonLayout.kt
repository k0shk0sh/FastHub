package com.fastaccess.github.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.fastaccess.data.model.ReactionContent
import com.fastaccess.data.model.ReactionGroupModel
import com.fastaccess.data.model.getEmoji
import com.fastaccess.github.R
import com.fastaccess.github.platform.works.ReactionWorker
import com.fastaccess.github.utils.extensions.popupEmoji
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
    private fun initReactions(
        reactionGroups: List<ReactionGroupModel>?
    ) {
        getReactionCount(reactionGroups, ReactionContent.THUMBS_UP).let {
            if (it > 0) {
                thumbsUp.isVisible = true
                thumbsUp.text = "${ReactionContent.THUMBS_UP.getEmoji()} $it"
            } else {
                thumbsUp.isVisible = false
            }
        }

        getReactionCount(reactionGroups, ReactionContent.THUMBS_DOWN).let {
            if (it > 0) {
                thumbsDown.isVisible = true
                thumbsDown.text = "${ReactionContent.THUMBS_DOWN.getEmoji()} $it"
            } else {
                thumbsDown.isVisible = false
            }
        }

        getReactionCount(reactionGroups, ReactionContent.CONFUSED).let {
            if (it > 0) {
                confused.isVisible = true
                confused.text = "${ReactionContent.CONFUSED.getEmoji()} $it"
            } else {
                confused.isVisible = false
            }
        }

        getReactionCount(reactionGroups, ReactionContent.LAUGH).let {
            if (it > 0) {
                laugh.isVisible = true
                laugh.text = "${ReactionContent.LAUGH.getEmoji()} $it"
            } else {
                laugh.isVisible = false
            }
        }

        getReactionCount(reactionGroups, ReactionContent.HOORAY).let {
            if (it > 0) {
                hooray.isVisible = true
                hooray.text = "${ReactionContent.HOORAY.getEmoji()} $it"
            } else {
                hooray.isVisible = false
            }
        }

        getReactionCount(reactionGroups, ReactionContent.HEART).let {
            if (it > 0) {
                heart.isVisible = true
                heart.text = "${ReactionContent.HEART.getEmoji()} $it"
            } else {
                heart.isVisible = false
            }
        }

        getReactionCount(reactionGroups, ReactionContent.ROCKET).let {
            if (it > 0) {
                rocket.isVisible = true
                rocket.text = "${ReactionContent.ROCKET.getEmoji()} $it"
            } else {
                rocket.isVisible = false
            }
        }

        getReactionCount(reactionGroups, ReactionContent.EYES).let {
            if (it > 0) {
                eyes.isVisible = true
                eyes.text = "${ReactionContent.EYES.getEmoji()} $it"
            } else {
                eyes.isVisible = false
            }
        }

    }

    private fun getReactionCount(
        reactionGroups: List<ReactionGroupModel>?,
        content: ReactionContent
    ): Int = reactionGroups
        ?.filter { it.users != null && it.content != null }
        ?.firstOrNull { it.content == content }?.let {
            return@let it.users?.totalCount ?: 0
        } ?: 0

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