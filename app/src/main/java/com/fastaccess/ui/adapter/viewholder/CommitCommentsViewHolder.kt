package com.fastaccess.ui.adapter.viewholder

import android.support.transition.ChangeBounds
import android.support.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import butterknife.BindView
import com.fastaccess.R
import com.fastaccess.data.dao.model.Comment
import com.fastaccess.helper.InputHelper
import com.fastaccess.helper.ParseDateFormat
import com.fastaccess.helper.ViewHelper
import com.fastaccess.provider.markdown.MarkDownProvider
import com.fastaccess.provider.scheme.LinkParserHelper
import com.fastaccess.provider.timeline.handler.drawable.DrawableGetter
import com.fastaccess.ui.adapter.callback.OnToggleView
import com.fastaccess.ui.widgets.AvatarLayout
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.ForegroundImageView
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder

/**
 * Created by kosh on 15/08/2017.
 */
class CommitCommentsViewHolder private constructor(view: View, adapter: BaseRecyclerAdapter<*, *, *>,
                                                   val viewGroup: ViewGroup, val onToggleView: OnToggleView)
    : BaseViewHolder<Comment>(view, adapter) {

    init {
        if (adapter.getRowWidth() == 0) {
            itemView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    itemView.viewTreeObserver.removeOnPreDrawListener(this)
                    adapter.setRowWidth(itemView.width - ViewHelper.dpToPx(itemView.context, 48f))
                    return false
                }
            })
        }
        itemView.setOnClickListener(null)
        itemView.setOnLongClickListener(null)
        commentMenu.setOnClickListener(this)
        commentMenu.setOnLongClickListener(this)
        toggle.visibility = View.GONE
        commentMenu.visibility = View.GONE
        commentOptions.visibility = View.GONE
    }

    @BindView(R.id.avatarView) lateinit var avatar: AvatarLayout
    @BindView(R.id.name) lateinit var name: FontTextView
    @BindView(R.id.date) lateinit var date: FontTextView
    @BindView(R.id.toggle) lateinit var toggle: ForegroundImageView
    @BindView(R.id.commentMenu) lateinit var commentMenu: ForegroundImageView
    @BindView(R.id.comment) lateinit var comment: FontTextView
    @BindView(R.id.commentOptions) lateinit var commentOptions: View
    @BindView(R.id.owner) lateinit var owner: TextView

    override fun onClick(v: View) {
        if (v.id == R.id.toggle || v.id == R.id.toggleHolder) {
            val position = adapterPosition
            onToggleView.onToggle(position.toLong(), !onToggleView.isCollapsed(position.toLong()))
            onToggle(onToggleView.isCollapsed(position.toLong()), true)
        } else {
            super.onClick(v)
        }
    }

    override fun bind(t: Comment) {
        val author3 = t.user
        if (author3 != null) {
            avatar.setUrl(author3.avatarUrl, author3.login, false, LinkParserHelper.isEnterprise(author3.url))
            name.text = author3.login
        } else {
            avatar.setUrl(null, null, false, false)
            name.text = ""
        }
        if (!InputHelper.isEmpty(t.body)) {
            val width = adapter?.getRowWidth() ?: 0
            if (width > 0) {
                MarkDownProvider.setMdText(comment, t.body, width)
            } else {
                MarkDownProvider.setMdText(comment, t.body)
            }
        } else {
            comment.text = ""
        }
        if (t.authorAssociation != null && !"none".equals(t.authorAssociation, ignoreCase = true)) {
            owner.text = t.authorAssociation.toLowerCase()
            owner.visibility = View.VISIBLE
        } else {
            owner.visibility = View.GONE
        }
        if (t.createdAt == t.updatedAt) {
            date.text = String.format("%s %s", ParseDateFormat.getTimeAgo(t.updatedAt), itemView
                    .resources.getString(R.string.edited))
        } else {
            date.text = ParseDateFormat.getTimeAgo(t.createdAt)
        }
        onToggle(onToggleView.isCollapsed(adapterPosition.toLong()), false)
    }

    private fun onToggle(expanded: Boolean, animate: Boolean) {
        if (animate) {
            TransitionManager.beginDelayedTransition(viewGroup, ChangeBounds())
        }
        toggle.rotation = if (!expanded) 0.0f else 180f
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