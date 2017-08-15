package com.fastaccess.ui.adapter.viewholder

import android.support.transition.ChangeBounds
import android.support.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import butterknife.BindView
import com.fastaccess.R
import com.fastaccess.data.dao.ReactionsModel
import com.fastaccess.data.dao.timeline.PullRequestCommitModel
import com.fastaccess.helper.InputHelper
import com.fastaccess.helper.ParseDateFormat
import com.fastaccess.provider.scheme.LinkParserHelper
import com.fastaccess.provider.timeline.CommentsHelper
import com.fastaccess.provider.timeline.HtmlHelper
import com.fastaccess.provider.timeline.handler.drawable.DrawableGetter
import com.fastaccess.ui.adapter.callback.OnToggleView
import com.fastaccess.ui.widgets.AvatarLayout
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.ForegroundImageView
import com.fastaccess.ui.widgets.SpannableBuilder
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder
import pr.type.ReactionContent

/**
 * Created by kosh on 15/08/2017.
 */
class CommitCommentsViewHolder private constructor(view: View, adapter: BaseRecyclerAdapter<*, *, *>,
                                                   val viewGroup: ViewGroup, val onToggleView: OnToggleView)
    : BaseViewHolder<PullRequestCommitModel>(view, adapter) {

    init {
        itemView.setOnClickListener(null)
        itemView.setOnLongClickListener(null)
        commentMenu.setOnClickListener(this)
        commentMenu.setOnLongClickListener(this)
        toggleHolder.setOnClickListener(this)
        toggle.setOnClickListener(this)
        laugh.setOnClickListener(this)
        sad.setOnClickListener(this)
        thumbsDown.setOnClickListener(this)
        thumbsUp.setOnClickListener(this)
        hurray.setOnClickListener(this)
        laugh.setOnLongClickListener(this)
        sad.setOnLongClickListener(this)
        thumbsDown.setOnLongClickListener(this)
        thumbsUp.setOnLongClickListener(this)
        hurray.setOnLongClickListener(this)
        heart.setOnLongClickListener(this)
        heart.setOnClickListener(this)
    }

    @BindView(R.id.avatarView) lateinit var avatar: AvatarLayout
    @BindView(R.id.name) lateinit var name: FontTextView
    @BindView(R.id.date) lateinit var date: FontTextView
    @BindView(R.id.toggle) lateinit var toggle: ForegroundImageView
    @BindView(R.id.commentMenu) lateinit var commentMenu: ForegroundImageView
    @BindView(R.id.toggleHolder) lateinit var toggleHolder: LinearLayout
    @BindView(R.id.thumbsUp) lateinit var thumbsUp: FontTextView
    @BindView(R.id.thumbsDown) lateinit var thumbsDown: FontTextView
    @BindView(R.id.laugh) lateinit var laugh: FontTextView
    @BindView(R.id.hurray) lateinit var hurray: FontTextView
    @BindView(R.id.sad) lateinit var sad: FontTextView
    @BindView(R.id.heart) lateinit var heart: FontTextView
    @BindView(R.id.emojiesList) lateinit var emojiesList: HorizontalScrollView
    @BindView(R.id.commentOptions) lateinit var commentOptions: RelativeLayout
    @BindView(R.id.comment) lateinit var comment: FontTextView
    @BindView(R.id.reactionsText) lateinit var reactionsText: FontTextView
    @BindView(R.id.owner) lateinit var owner: FontTextView

    override fun bind(t: PullRequestCommitModel) {
        val commentsModel = t.node1
        val author3 = commentsModel.author()
        owner.visibility = View.VISIBLE
        owner.text = if ("none".equals(commentsModel.authorAssociation().name.toLowerCase(), ignoreCase = true)) ""
        else commentsModel.authorAssociation().name.toLowerCase()
        if (author3 != null) {
            avatar.setUrl(author3.avatarUrl().toString(), author3.login(), false, LinkParserHelper.isEnterprise(author3.url().toString()))
            name.text = author3.login()
        } else {
            avatar.setUrl(null, null, false, false)
            name.text = null
        }
        if (!InputHelper.isEmpty(commentsModel.bodyHTML())) {
            HtmlHelper.htmlIntoTextView(comment, commentsModel.bodyHTML().toString(), viewGroup.width)
        } else {
            comment.text = ""
        }
        if (commentsModel.createdAt() == commentsModel.lastEditedAt()) {
            date.text = String.format("%s %s", ParseDateFormat.getTimeAgo(commentsModel.lastEditedAt()!!.toString()), itemView
                    .resources.getString(R.string.edited))
        } else {
            date.text = ParseDateFormat.getTimeAgo(commentsModel.createdAt().toString())
        }
        appendEmojies(t.reaction)
        emojiesList.visibility = View.VISIBLE
        if (onToggleView != null) onToggle(onToggleView.isCollapsed(adapterPosition.toLong()), false)
    }

    private fun addReactionCount(v: View) {
        if (adapter != null) {
            val timelineModel = adapter.getItemByPosition(adapterPosition) as PullRequestCommitModel ?: return
            val reactions = timelineModel.reaction
            if (reactions != null && !reactions.isEmpty()) {
                val reactionIndex = getReaction(v.id, reactions)
                if (reactionIndex != -1) {
                    val reaction = reactions[reactionIndex]
                    if (!reaction.viewerHasReacted) {
                        reaction.viewerHasReacted = true
                        reaction.total_count = reaction.total_count + 1
                    } else {
                        reaction.viewerHasReacted = false
                        reaction.total_count = reaction.total_count - 1
                    }
                    reactions[reactionIndex] = reaction
                }
                appendEmojies(reactions)
                timelineModel.reaction = reactions
            }
        }
    }

    private fun getReaction(id: Int, reactionGroup: List<ReactionsModel>): Int {
        for (i in reactionGroup.indices) {
            val reactionGroup1 = reactionGroup[i]
            if (id == R.id.heart && reactionGroup1.content.equals(ReactionContent.HEART.name, ignoreCase = true)) {
                return i
            } else if (id == R.id.sad && reactionGroup1.content.equals(ReactionContent.CONFUSED.name, ignoreCase = true)) {
                return i
            } else if (id == R.id.hurray && reactionGroup1.content.equals(ReactionContent.HOORAY.name, ignoreCase = true)) {
                return i
            } else if (id == R.id.laugh && reactionGroup1.content.equals(ReactionContent.LAUGH.name, ignoreCase = true)) {
                return i
            } else if (id == R.id.thumbsDown && reactionGroup1.content.equals(ReactionContent.THUMBS_DOWN.name, ignoreCase = true)) {
                return i
            } else if (id == R.id.thumbsUp && reactionGroup1.content.equals(ReactionContent.THUMBS_UP.name, ignoreCase = true)) {
                return i
            }
        }
        return -1
    }

    private fun appendEmojies(reactions: List<ReactionsModel>) {
        reactionsText.text = ""
        val spannableBuilder = SpannableBuilder.builder()
        for (reaction in reactions) {
            var charSequence: CharSequence? = null
            if (reaction.content.equals(ReactionContent.THUMBS_UP.name, ignoreCase = true)) {
                charSequence = SpannableBuilder.builder()
                        .append(CommentsHelper.getThumbsUp()).append(" ")
                        .append(reaction.total_count.toString())
                        .append("   ")
                thumbsUp.text = charSequence
            } else if (reaction.content.equals(ReactionContent.THUMBS_DOWN.name, ignoreCase = true)) {
                charSequence = SpannableBuilder.builder()
                        .append(CommentsHelper.getThumbsDown()).append(" ")
                        .append(reaction.total_count.toString())
                        .append("   ")
                thumbsDown.text = charSequence
            } else if (reaction.content.equals(ReactionContent.LAUGH.name, ignoreCase = true)) {
                charSequence = SpannableBuilder.builder()
                        .append(CommentsHelper.getLaugh()).append(" ")
                        .append(reaction.total_count.toString())
                        .append("   ")
                laugh.text = charSequence
            } else if (reaction.content.equals(ReactionContent.HOORAY.name, ignoreCase = true)) {
                charSequence = SpannableBuilder.builder()
                        .append(CommentsHelper.getHooray()).append(" ")
                        .append(reaction.total_count.toString())
                        .append("   ")
                hurray.text = charSequence
            } else if (reaction.content.equals(ReactionContent.HEART.name, ignoreCase = true)) {
                charSequence = SpannableBuilder.builder()
                        .append(CommentsHelper.getHeart()).append(" ")
                        .append(reaction.total_count.toString())
                        .append("   ")
                heart.text = charSequence
            } else if (reaction.content.equals(ReactionContent.CONFUSED.name, ignoreCase = true)) {
                charSequence = SpannableBuilder.builder()
                        .append(CommentsHelper.getSad()).append(" ")
                        .append(reaction.total_count.toString())
                        .append("   ")
                sad.text = charSequence
            }
            if (charSequence != null && reaction.total_count > 0) {
                spannableBuilder.append(charSequence)
            }
        }
        if (spannableBuilder.length > 0) {
            reactionsText.text = spannableBuilder
            if (!onToggleView.isCollapsed(adapterPosition.toLong())) {
                reactionsText.visibility = View.VISIBLE
            }
        } else {
            reactionsText.visibility = View.GONE
        }
    }

    private fun onToggle(expanded: Boolean, animate: Boolean) {
        if (animate) {
            TransitionManager.beginDelayedTransition(viewGroup, ChangeBounds())
        }
        toggle.rotation = if (!expanded) 0.0f else 180f
        commentOptions.visibility = if (!expanded) View.GONE else View.VISIBLE
        if (!InputHelper.isEmpty(reactionsText)) {
            reactionsText.visibility = if (!expanded) View.VISIBLE else View.GONE
        }
    }

    override fun onViewIsDetaching() {
        val drawableGetter = comment.getTag(R.id.drawable_callback) as DrawableGetter?
        drawableGetter?.clear(drawableGetter)
    }

    companion object {
        fun newInstance(parent: ViewGroup, adapter: BaseRecyclerAdapter<*, *, *>, onToggleView: OnToggleView): CommitCommentsViewHolder {
            return CommitCommentsViewHolder(getView(parent, R.layout.comments_row_item), adapter, parent, onToggleView)
        }
    }
}